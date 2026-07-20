package com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.AlocacaoStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MochilaPrioridadeAlocacaoStrategyTest {

    private final AlocacaoStrategy strategy = new MochilaPrioridadeAlocacaoStrategy();

    @Test
    void shouldAllocatePedidosRespectingCapacity() {
        Drone drone = Drone.builder()
                .id(1L)
                .capacidadeMaxKg(10.0)
                .statusAtual(StatusDrone.IDLE)
                .build();
        List<Pedido> pedidos = List.of(
                Pedido.builder().peso(6.0).prioridade(Prioridade.ALTA).build(),
                Pedido.builder().peso(4.0).prioridade(Prioridade.MEDIA).build(),
                Pedido.builder().peso(5.0).prioridade(Prioridade.BAIXA).build()
        );

        Map<Drone, List<Pedido>> alocacao = strategy.otimizarAlocacao(pedidos, List.of(drone));

        assertThat(alocacao).containsKey(drone);
        List<Pedido> alocados = alocacao.get(drone);
        assertThat(alocados).hasSize(2);
        double totalPeso = alocados.stream().mapToDouble(Pedido::getPeso).sum();
        assertThat(totalPeso).isLessThanOrEqualTo(10.0);
    }

    @Test
    void shouldNotAllocateWhenDroneHasNoCapacity() {
        Drone drone = Drone.builder()
                .capacidadeMaxKg(2.0)
                .statusAtual(StatusDrone.IDLE)
                .build();
        List<Pedido> pedidos = List.of(
                Pedido.builder().peso(3.0).prioridade(Prioridade.ALTA).build()
        );

        Map<Drone, List<Pedido>> alocacao = strategy.otimizarAlocacao(pedidos, List.of(drone));

        assertThat(alocacao).isEmpty();
    }
}