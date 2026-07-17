package com.example.Simulador_de_Encomendas_em_Drone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record DroneRequestDTO(
    @NotBlank(message = "O identificador é obrigatório")
    String identificador,
    
    @Positive(message = "A capacidade máxima deve ser maior que zero")
    double capacidadeMaxKg,
    
    @Positive(message = "A autonomia máxima deve ser maior que zero")
    double autonomiaMaxKm
) {}