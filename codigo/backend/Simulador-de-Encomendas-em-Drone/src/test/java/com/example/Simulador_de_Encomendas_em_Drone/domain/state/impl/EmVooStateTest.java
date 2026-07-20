package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmVooStateTest {

    @Test
    void shouldMoveTowardDestination() {
        Drone drone = Drone.builder()
                .posXAtual(0.0)
                .posYAtual(0.0)
                .capacidadeMaxKg(10.0)
                .bateriaAtual(100.0)
                .build();
        Pedido pedido = Pedido.builder()
                .posXDestino(10.0)
                .posYDestino(10.0)
                .peso(2.0)
                .build();
        drone.setPedidosAlocados(List.of(pedido));

        EmVooState state = new EmVooState();
        state.processarTick(drone);

        assertThat(drone.getPosXAtual()).isGreaterThan(0.0);
        assertThat(drone.getPosYAtual()).isGreaterThan(0.0);
        assertThat(drone.getBateriaAtual()).isLessThan(100.0);
    }

    @Test
    void shouldTransitionToEntregandoWhenArrives() {
        Drone drone = Drone.builder()
                .posXAtual(5.0)
                .posYAtual(5.0)
                .capacidadeMaxKg(10.0)
                .bateriaAtual(100.0)
                .build();
        Pedido pedido = Pedido.builder()
                .posXDestino(5.0)
                .posYDestino(5.0)
                .peso(1.0)
                .build();
        drone.setPedidosAlocados(List.of(pedido));

        EmVooState state = new EmVooState();
        state.processarTick(drone);

        assertThat(drone.getStatusAtual()).isEqualTo(StatusDrone.ENTREGANDO);
    }
}