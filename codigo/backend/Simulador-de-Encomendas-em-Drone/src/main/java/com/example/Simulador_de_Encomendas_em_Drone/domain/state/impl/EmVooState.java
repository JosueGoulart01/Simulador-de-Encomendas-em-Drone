package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.RoteamentoStrategy;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.impl.RetaFugindoObstaculoStrategy;

import java.util.Collections;

public class EmVooState implements DroneState {

    private final RoteamentoStrategy roteamentoStrategy = new RetaFugindoObstaculoStrategy();
    private static final double VELOCIDADE_DRONE = 2.0;
    private static final double LIMITE_PROXIMIDADE = 0.2; // Tolerância matemática para ponto flutuante

    @Override
    public void processarTick(Drone drone) {
        if (drone.getPedidosAlocados() == null || drone.getPedidosAlocados().isEmpty()) {
            drone.inicializarEstado(new RetornandoState());
            return;
        }

        Pedido pedidoAtual = drone.getPedidosAlocados().get(0);

        // Calcula o próximo passo geométrico
        double[] proximaPosicao = roteamentoStrategy.calcularProximoPasso(
                drone.getPosXAtual(), drone.getPosYAtual(),
                pedidoAtual.getPosXDestino(), pedidoAtual.getPosYDestino(),
                VELOCIDADE_DRONE, Collections.emptyList()
        );

        drone.atualizarPosicao(proximaPosicao[0], proximaPosicao[1]);

        // Gasto de bateria proporcional ao deslocamento e peso
        double fatorCarga = 1.0 + (pedidoAtual.getPeso() / drone.getCapacidadeMaxKg());
        drone.consumirBateria(VELOCIDADE_DRONE * 0.5 * fatorCarga);

        // Verificação por aproximação delta (Evita que o drone vibre infinito no mesmo lugar)
        boolean chegouX = Math.abs(drone.getPosXAtual() - pedidoAtual.getPosXDestino()) <= LIMITE_PROXIMIDADE;
        boolean chegouY = Math.abs(drone.getPosYAtual() - pedidoAtual.getPosYDestino()) <= LIMITE_PROXIMIDADE;

        if (chegouX && chegouY) {
            // Seta a posição exata para limpar resíduos matemáticos antes da entrega
            drone.atualizarPosicao(pedidoAtual.getPosXDestino(), pedidoAtual.getPosYDestino());
            drone.inicializarEstado(new EntregandoState());
        }
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.EM_VOO;
    }
}