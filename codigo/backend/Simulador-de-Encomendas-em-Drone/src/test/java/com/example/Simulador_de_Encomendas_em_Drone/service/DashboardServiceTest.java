package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DashboardResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.repository.DroneRepository;
import com.example.Simulador_de_Encomendas_em_Drone.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DroneRepository droneRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnDashboardData() {
        // Arrange
        when(pedidoRepository.countByStatus(StatusPedido.ENTREGUE)).thenReturn(5L);
        when(pedidoRepository.countByStatus(StatusPedido.FILA)).thenReturn(3L);

        Drone drone1 = Drone.builder().id(1L).identificador("D1").statusAtual(StatusDrone.EM_VOO).bateriaAtual(80.0).build();
        Drone drone2 = Drone.builder().id(2L).identificador("D2").statusAtual(StatusDrone.IDLE).bateriaAtual(95.0).build();
        when(droneRepository.findAll()).thenReturn(List.of(drone1, drone2));

        when(pedidoRepository.findDroneMaisEficiente()).thenReturn("D1");
        when(pedidoRepository.findTop10ByStatusOrderByIdDesc(StatusPedido.ENTREGUE))
                .thenReturn(List.of());

        // Act
        DashboardResponseDTO result = dashboardService.obterDadosDashboard();

        // Assert
        assertThat(result.totalEntregasRealizadas()).isEqualTo(5);
        assertThat(result.totalPedidosAguardando()).isEqualTo(3);
        assertThat(result.dronesTotal()).isEqualTo(2);
        assertThat(result.dronesEmVoo()).isEqualTo(1);
        assertThat(result.dronesOciosos()).isEqualTo(1);
        assertThat(result.droneMaisEficiente()).isEqualTo("D1");
    }
}