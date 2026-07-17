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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimulacaoEngine {

    private final DroneRepository droneRepository;
    private final PedidoRepository pedidoRepository;
    private final AlocacaoStrategy alocacaoStrategy;

    @Scheduled(fixedRate = 2000)
    @Transactional
    public void processarCicloSimulacao() {
        log.info("Iniciando novo ciclo de simulação (Tick)...");

        // 1. Carrega todos os drones e sincroniza a instância polimórfica de estado baseando-se no banco
        List<Drone> todosDrones = droneRepository.findAll();
        todosDrones.forEach(this::reconstruirEstadoInstancia);

        // 2. Aloca novos pedidos da fila exclusivamente para drones ociosos (IDLE)
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
                    p.alterarStatus(StatusPedido.ALOCADO); // Começa rigidamente como ALOCADO
                });
                drone.setPedidosAlocados(pedidos);
                drone.inicializarEstado(new CarregandoState());
                
                // Salva imediatamente a transição para ALOCADO
                pedidoRepository.saveAll(pedidos);
            });
        }

        // 3. Executa a física do ciclo (movimentação, gasto de bateria e alteração de estados dos pedidos)
        todosDrones.forEach(drone -> {
            if (drone.getStatusAtual() != StatusDrone.IDLE || drone.getBateriaAtual() < 100.0) {
                // Cria uma cópia da lista de pedidos antes de rodar o processamento do State.
                // Isso previne que percamos a referência dos objetos caso o EntregandoState remova o pacote da lista.
                List<Pedido> pedidosAntesDoTick = new ArrayList<>(drone.getPedidosAlocados());

                drone.executarAcaoSimulacao();

                // Garante que qualquer mutação de status feita pela State Machine (ex: para EM_TRANSITO ou ENTREGUE)
                // seja explicitamente capturada e guardada pelo Hibernate de volta no PostgreSQL
                if (!pedidosAntesDoTick.isEmpty()) {
                    pedidoRepository.saveAll(pedidosAntesDoTick);
                }
            }
        });

        // 4. Salva o estado físico atualizado de toda a frota de drones no banco
        droneRepository.saveAll(todosDrones);
    }

    /**
     * Sincroniza o atributo transiente @Transient de estado em memória com o status real persistido no PostgreSQL.
     */
    private void reconstruirEstadoInstancia(Drone drone) {
        switch (drone.getStatusAtual()) {
            case CARREGANDO -> drone.inicializarEstado(new CarregandoState());
            case EM_VOO -> drone.inicializarEstado(new EmVooState());
            case ENTREGANDO -> drone.inicializarEstado(new EntregandoState());
            case RETORNANDO -> drone.inicializarEstado(new RetornandoState());
            default -> drone.inicializarEstado(new IdleState());
        }
    }
}