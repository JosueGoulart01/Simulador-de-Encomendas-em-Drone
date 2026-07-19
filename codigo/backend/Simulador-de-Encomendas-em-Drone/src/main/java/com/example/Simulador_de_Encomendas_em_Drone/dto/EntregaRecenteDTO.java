package com.example.Simulador_de_Encomendas_em_Drone.dto;

import java.time.LocalDateTime;

public record EntregaRecenteDTO(
    Long pedidoId,
    String identificadorDrone,
    double peso,
    double posXDestino,
    double posYDestino,
    String prioridade,
    LocalDateTime dataConclusao
) {}