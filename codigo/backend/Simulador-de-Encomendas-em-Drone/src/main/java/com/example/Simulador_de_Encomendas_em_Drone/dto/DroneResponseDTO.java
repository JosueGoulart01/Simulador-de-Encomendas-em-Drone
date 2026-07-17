package com.example.Simulador_de_Encomendas_em_Drone.dto;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;

public record DroneResponseDTO(
    Long id,
    String identificador,
    double capacidadeMaxKg,
    double autonomiaMaxKm,
    double bateriaAtual,
    StatusDrone statusAtual,
    double posXAtual,
    double posYAtual
) {}