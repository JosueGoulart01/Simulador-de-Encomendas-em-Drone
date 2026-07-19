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

        // 2. Estatísticas da Frota
        List<Drone> frota = droneRepository.findAll();
        long dronesTotal = frota.size();
        long dronesEmVoo = frota.stream().filter(d -> d.getStatusAtual() == StatusDrone.EM_VOO || d.getStatusAtual() == StatusDrone.RETORNANDO).count();
        long dronesOciosos = frota.stream().filter(d -> d.getStatusAtual() == StatusDrone.IDLE).count();

        // 3. Drone Líder em Performance
        String droneTop = pedidoRepository.findDroneMaisEficiente();
        if (droneTop == null) {
            droneTop = "Nenhuma entrega registrada";
        }

        // 4. Cálculo de Tempo Médio de Ciclo Logístico (Criação até a Entrega)
        double tempoMedio = 0.0;
        List<Pedido> todosEntregues = pedidoRepository.findTop10ByStatusOrderByIdDesc(StatusPedido.ENTREGUE);
        
        if (!todosEntregues.isEmpty()) {
            long totalMinutos = todosEntregues.stream()
                    .mapToLong(p -> Duration.between(p.getDataCriacao(), LocalDateTime.now()).toMinutes())
                    .sum();
            tempoMedio = (double) totalMinutos / todosEntregues.size();
        }

        // 5. Mapeia a lista das últimas entregas realizadas (Para alimentar a tabela do Front)
        List<EntregaRecenteDTO> ultimasEntregas = todosEntregues.stream()
                .map(p -> new EntregaRecenteDTO(
                        p.getId(),
                        p.getDrone() != null ? p.getDrone().getIdentificador() : "Desvinculado",
                        p.getPeso(),
                        p.getPosXDestino(),
                        p.getPosYDestino(),
                        p.getPrioridade().name(),
                        LocalDateTime.now() // Em um cenário real, você teria um campo 'dataEntrega' no Pedido
                )).toList();

        return new DashboardResponseDTO(
                entregues, naFila, tempoMedio, droneTop,
                dronesTotal, dronesEmVoo, dronesOciosos,
                ultimasEntregas
        );
    }
}