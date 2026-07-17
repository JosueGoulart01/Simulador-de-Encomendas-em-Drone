package com.example.Simulador_de_Encomendas_em_Drone.domain.state;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;

public interface DroneState {
    void processarTick(Drone drone);
    StatusDrone getStatus();
}