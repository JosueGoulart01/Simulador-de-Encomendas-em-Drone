package com.example.Simulador_de_Encomendas_em_Drone.engine;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl.*;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.AlocacaoStrategy;
import com.example.Simulador_de_Encomendas_em_Drone.repository.DroneRepository;
import com.example.Simulador_de_Encomendas_em_Drone.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimulacaoEngine {

    private final DroneRepository droneRepository;
    private final PedidoRepository pedidoRepository;
    private final AlocacaoStrategy alocacaoStrategy;

    /**
     * Executa a cada 2 segundos (2000ms), simulando a passagem de tempo do sistema.
     * Aloca novos pedidos e atualiza o deslocamento de todos os drones em voo.
     */
    @Scheduled(fixedRate = 2000)
    @Transactional
    public void processarCicloSimulacao() {
        log.info("Iniciando novo ciclo de simulação (Tick)...");

        // 1. Carrega todos os drones do banco e reconstrói suas instâncias de estado em memória
        List<Drone> todosDrones = droneRepository.findAll();
        todosDrones.forEach(this::reconstruirEstadoInstancia);

        // 2. Tenta alocar pedidos da fila para drones que estão IDLE
        List<Pedido> pedidosNaFila = pedidoRepository.findByStatusOrderByPrioridadeDescDataCriacaoAsc(StatusPedido.FILA);
        List<Drone> dronesDisponiveis = todosDrones.stream()
                .filter(d -> d.getStatusAtual() == StatusDrone.IDLE)
                .toList();

        if (!pedidosNaFila.isEmpty() && !dronesDisponiveis.isEmpty()) {
            Map<Drone, List<Pedido>> novasAlocacoes = alocacaoStrategy.otimizarAlocacao(pedidosNaFila, dronesDisponiveis);

            novasAlocacoes.forEach((drone, pedidos) -> {
                log.info("Alocando {} pedido(s) para o drone {}", pedidos.size(), drone.getIdentificador());
                pedidos.forEach(p -> {
                    p.setDrone(drone);
                    p.alterarStatus(StatusPedido.ALOCADO);
                });
                drone.setPedidosAlocados(pedidos);
                drone.inicializarEstado(new CarregandoState()); // Transiciona para o primeiro estado do ciclo
            });
        }

        // 3. Executa a lógica de física, bateria e rota para todos os drones ativos no ciclo
        todosDrones.forEach(drone -> {
            if (drone.getStatusAtual() != StatusDrone.IDLE || drone.getBateriaAtual() < 100.0) {
                drone.executarAcaoSimulacao();
            }
        });

        // 4. Salva o novo panorama de estados de volta no banco (PostgreSQL)
        droneRepository.saveAll(todosDrones);
    }

    /**
     * Sincroniza o estado polimórfico (padrão State) baseado na string persistida no banco.
     * Essencial para que o Hibernate não perca a referência de qual classe de estado o drone pertence.
     */
    private void reconstruirEstadoInstancia(Drone drone) {
        if (drone.getEstadoInstancia() == null) {
            switch (drone.getStatusAtual()) {
                case CARREGANDO -> drone.inicializarEstado(new CarregandoState());
                case EM_VOO -> drone.inicializarEstado(new EmVooState());
                case ENTREGANDO -> drone.inicializarEstado(new EntregandoState());
                case RETORNANDO -> drone.inicializarEstado(new RetornandoState());
                default -> drone.inicializarEstado(new IdleState());
            }
        }
    }
}