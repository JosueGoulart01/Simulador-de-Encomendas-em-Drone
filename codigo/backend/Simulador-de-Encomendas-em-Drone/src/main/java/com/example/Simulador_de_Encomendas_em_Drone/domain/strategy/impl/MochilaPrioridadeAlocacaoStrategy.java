package com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.AlocacaoStrategy;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MochilaPrioridadeAlocacaoStrategy implements AlocacaoStrategy {

    @Override
    public Map<Drone, List<Pedido>> otimizarAlocacao(List<Pedido> pedidosFila, List<Drone> dronesDisponiveis) {
        Map<Drone, List<Pedido>> mapaAlocacao = new HashMap<>();
        List<Pedido> copiaFila = new ArrayList<>(pedidosFila);

        for (Drone drone : dronesDisponiveis) {
            double capacidadeRestante = drone.getCapacidadeMaxKg();
            List<Pedido> pacotesParaVoo = new ArrayList<>();

            // Itera pela fila (que já vem ordenada por prioridade no repository)
            Iterator<Pedido> iterator = copiaFila.iterator();
            while (iterator.hasNext()) {
                Pedido pedido = iterator.next();

                // Regra de Validação Básica: Cabe no Drone atual?
                if (pedido.getPeso() <= capacidadeRestante) {
                    pacotesParaVoo.add(pedido);
                    capacidadeRestante -= pedido.getPeso();
                    iterator.remove(); // Remove da fila temporária para não alocar duplicado
                }
            }

            if (!pacotesParaVoo.isEmpty()) {
                mapaAlocacao.put(drone, pacotesParaVoo);
            }
        }

        return mapaAlocacao;
    }
}