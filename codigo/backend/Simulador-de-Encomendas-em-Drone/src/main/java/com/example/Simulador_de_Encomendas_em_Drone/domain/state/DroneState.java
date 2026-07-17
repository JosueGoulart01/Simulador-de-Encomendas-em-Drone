package com.example.Simulador_de_Encomendas_em_Drone.domain.state;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;

public interface DroneState {
    void processarTick(Drone drone);
    StatusDrone getStatus();
}