package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DashboardResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterDadosDashboard() {
        long entregues = pedidoRepository.countByStatus(StatusPedido.ENTREGUE);
        long naFila = pedidoRepository.countByStatus(StatusPedido.FILA);
        
        // Descobre o drone líder em entregas
        String droneTop = pedidoRepository.findDroneMaisEficiente();
        if (droneTop == null) {
            droneTop = "Nenhum drone realizou entregas ainda";
        }

        // Simulação do cálculo de tempo médio (Baseado na criação do pedido até o momento atual para os entregues)
        // Em um cenário real com histórico, usaríamos um campo 'data_entrega' na tabela.
        double tempoMedio = 0.0;
        List<Pedido> todosEntregues = pedidoRepository.findAll().stream()
                .filter(p -> p.getStatus() == StatusPedido.ENTREGUE)
                .toList();

        if (!todosEntregues.isEmpty()) {
            long totalMinutos = todosEntregues.stream()
                    .mapToLong(p -> Duration.between(p.getDataCriacao(), LocalDateTime.now()).toMinutes())
                    .sum();
            tempoMedio = (double) totalMinutos / todosEntregues.size();
        }

        return new DashboardResponseDTO(entregues, naFila, tempoMedio, droneTop);
    }
}