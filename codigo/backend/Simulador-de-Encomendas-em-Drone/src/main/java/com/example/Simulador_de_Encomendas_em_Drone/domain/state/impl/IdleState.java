package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;

public class IdleState implements DroneState {

    @Override
    public void processarTick(Drone drone) {
        if (drone.getBateriaAtual() < 100.0) {
            drone.recarregar(); // Recarga automática na base (Diferencial)
        }
        // Permanece em repouso até que o motor de alocação mude seu estado para CARREGANDO
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.IDLE;
    }
}