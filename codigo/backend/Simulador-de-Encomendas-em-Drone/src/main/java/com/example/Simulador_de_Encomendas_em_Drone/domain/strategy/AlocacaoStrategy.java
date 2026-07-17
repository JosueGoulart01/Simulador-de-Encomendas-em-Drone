package com.example.Simulador_de_Encomendas_em_Drone.domain.strategy;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import java.util.List;
import java.util.Map;

public interface AlocacaoStrategy {
    /**
     * Combina os pedidos da fila com os drones disponíveis baseado em restrições de peso e autonomia.
     */
    Map<Drone, List<Pedido>> otimizarAlocacao(List<Pedido> pedidosFila, List<Drone> dronesDisponiveis);
}