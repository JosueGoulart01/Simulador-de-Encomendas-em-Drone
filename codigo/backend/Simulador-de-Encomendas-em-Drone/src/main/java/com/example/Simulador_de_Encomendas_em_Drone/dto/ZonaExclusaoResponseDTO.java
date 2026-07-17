package com.example.Simulador_de_Encomendas_em_Drone.dto;

public record ZonaExclusaoResponseDTO(
    Long id,
    String nome,
    double xMin,
    double yMin,
    double xMax,
    double yMax
) {}