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
    // Conta APENAS os pedidos que já foram fisicamente entregues no destino
    long entregues = pedidoRepository.countByStatus(StatusPedido.ENTREGUE);
    
    // Conta os pedidos que ainda estão na fila de espera aguardando drone
    long naFila = pedidoRepository.countByStatus(StatusPedido.FILA);
    
    String droneTop = pedidoRepository.findDroneMaisEficiente();
    if (droneTop == null) {
        droneTop = "Nenhum drone realizou entregas ainda";
    }

    double tempoMedio = 0.0;
    
    // Filtra estritamente os que já mudaram o status para ENTREGUE no banco
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