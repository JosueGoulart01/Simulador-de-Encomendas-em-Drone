package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;

public class CarregandoState implements DroneState {

    @Override
    public void processarTick(Drone drone) {
        // Simula o tempo gasto prendendo a carga física ao drone.
        // Após 1 tick carregando, ele muda de estado para iniciar a rota de voo.
        drone.inicializarEstado(new EmVooState());
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.CARREGANDO;
    }
}