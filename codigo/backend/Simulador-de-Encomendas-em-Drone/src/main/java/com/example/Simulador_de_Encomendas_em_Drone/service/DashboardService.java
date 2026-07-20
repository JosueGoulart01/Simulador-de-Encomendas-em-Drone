package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DashboardResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.EntregaRecenteDTO;
import com.example.Simulador_de_Encomendas_em_Drone.repository.DroneRepository;
import com.example.Simulador_de_Encomendas_em_Drone.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final DroneRepository droneRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterDadosDashboard() {
        // 1. Contadores de Pedidos
        long entregues = pedidoRepository.countByStatus(StatusPedido.ENTREGUE);
        long naFila = pedidoRepository.countByStatus(StatusPedido.FILA);
        long totalPedidos = pedidoRepository.count();
        long emTransito = pedidoRepository.countByStatus(StatusPedido.ALOCADO)
                        + pedidoRepository.countByStatus(StatusPedido.EM_TRANSITO);

        // 2. Estatísticas da Frota
        List<Drone> frota = droneRepository.findAll();
        long dronesTotal = frota.size();
        long dronesEmVoo = frota.stream()
                .filter(d -> d.getStatusAtual() == StatusDrone.EM_VOO || d.getStatusAtual() == StatusDrone.RETORNANDO)
                .count();
        long dronesOciosos = frota.stream().filter(d -> d.getStatusAtual() == StatusDrone.IDLE).count();
        long dronesCarregando = frota.stream().filter(d -> d.getStatusAtual() == StatusDrone.CARREGANDO).count();
        long dronesEntregando = frota.stream().filter(d -> d.getStatusAtual() == StatusDrone.ENTREGANDO).count();

        // 3. Bateria média e drone com menor bateria
        double bateriaMedia = frota.stream().mapToDouble(Drone::getBateriaAtual).average().orElse(0.0);
        String droneMenorBateria = frota.stream()
                .min(Comparator.comparingDouble(Drone::getBateriaAtual))
                .map(Drone::getIdentificador)
                .orElse("N/A");

        // 4. Drone mais eficiente
        String droneTop = pedidoRepository.findDroneMaisEficiente();
        if (droneTop == null) droneTop = "Nenhuma entrega registrada";

        // 5. Tempo médio de ciclo (apenas entregues)
        double tempoMedio = 0.0;
        List<Pedido> todosEntregues = pedidoRepository.findTop10ByStatusOrderByIdDesc(StatusPedido.ENTREGUE);
        if (!todosEntregues.isEmpty()) {
            long totalMinutos = todosEntregues.stream()
                    .mapToLong(p -> Duration.between(p.getDataCriacao(), LocalDateTime.now()).toMinutes())
                    .sum();
            tempoMedio = (double) totalMinutos / todosEntregues.size();
        }

        // 6. Últimas entregas
        List<EntregaRecenteDTO> ultimasEntregas = todosEntregues.stream()
                .map(p -> new EntregaRecenteDTO(
                        p.getId(),
                        p.getDrone() != null ? p.getDrone().getIdentificador() : "Desvinculado",
                        p.getPeso(),
                        p.getPosXDestino(),
                        p.getPosYDestino(),
                        p.getPrioridade().name(),
                        LocalDateTime.now() // idealmente teria dataEntrega, mantemos assim
                )).toList();

        return new DashboardResponseDTO(
                entregues, naFila, tempoMedio, droneTop,
                dronesTotal, dronesEmVoo, dronesOciosos,
                totalPedidos, emTransito, bateriaMedia, droneMenorBateria,
                dronesCarregando, dronesEntregando,
                ultimasEntregas
        );
    }
}