package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.RoteamentoStrategy;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.impl.RetaFugindoObstaculoStrategy;

import java.util.Collections;

public class RetornandoState implements DroneState {

    private final RoteamentoStrategy roteamentoStrategy = new RetaFugindoObstaculoStrategy();
    private static final double VELOCIDADE_DRONE = 2.0;
    private static final double LIMITE_PROXIMIDADE = 0.2; // Tolerância matemática para ponto flutuante

    @Override
    public void processarTick(Drone drone) {
        // Retorna em direção à Base Central (0.0, 0.0)
        double[] proximaPosicao = roteamentoStrategy.calcularProximoPasso(
                drone.getPosXAtual(), drone.getPosYAtual(),
                0.0, 0.0,
                VELOCIDADE_DRONE, Collections.emptyList()
        );

        drone.atualizarPosicao(proximaPosicao[0], proximaPosicao[1]);
        drone.consumirBateria(VELOCIDADE_DRONE * 0.3); // Consumo menor (sem peso de carga)

        // Verificação por aproximação delta do ponto de origem
        boolean chegouNaBaseX = Math.abs(drone.getPosXAtual() - 0.0) <= LIMITE_PROXIMIDADE;
        boolean chegouNaBaseY = Math.abs(drone.getPosYAtual() - 0.0) <= LIMITE_PROXIMIDADE;

        if (chegouNaBaseX && chegouNaBaseY) {
            drone.atualizarPosicao(0.0, 0.0);
            drone.inicializarEstado(new IdleState());
        }
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.RETORNANDO;
    }
}