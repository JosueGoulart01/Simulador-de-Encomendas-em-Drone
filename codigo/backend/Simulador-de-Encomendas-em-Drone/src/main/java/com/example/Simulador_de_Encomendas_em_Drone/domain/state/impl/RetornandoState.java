package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.RoteamentoStrategy;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.impl.RetaFugindoObstaculoStrategy;

public class RetornandoState implements DroneState {

    private final RoteamentoStrategy roteamentoStrategy = new RetaFugindoObstaculoStrategy();
    private static final double VELOCIDADE_DRONE = 2.0;

    @Override
    public void processarTick(Drone drone) {
        // O destino padrão de retorno é a base (0,0)
        double[] proximaPosicao = roteamentoStrategy.calcularProximoPasso(
                drone.getPosXAtual(), drone.getPosYAtual(),
                0.0, 0.0,
                VELOCIDADE_DRONE, java.util.Collections.emptyList()
        );

        drone.atualizarPosicao(proximaPosicao[0], proximaPosicao[1]);
        drone.consumirBateria(VELOCIDADE_DRONE * 0.3); // Gasta menos bateria porque voa sem peso

        // Chegou na base central? Volta a ficar ocioso e disponível
        if (drone.getPosXAtual() == 0.0 && drone.getPosYAtual() == 0.0) {
            drone.inicializarEstado(new IdleState());
        }
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.RETORNANDO;
    }
}