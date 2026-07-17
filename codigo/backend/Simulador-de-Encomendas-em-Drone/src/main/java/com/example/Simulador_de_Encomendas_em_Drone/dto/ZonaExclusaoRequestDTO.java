package com.example.Simulador_de_Encomendas_em_Drone.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ZonaExclusaoRequestDTO(
    @NotBlank(message = "O nome da zona de exclusão é obrigatório")
    String nome,

    @NotNull(message = "O X mínimo é obrigatório")
    Double xMin,

    @NotNull(message = "O Y mínimo é obrigatório")
    Double yMin,

    @NotNull(message = "O X máximo é obrigatório")
    Double xMax,

    @NotNull(message = "O Y máximo é obrigatório")
    Double yMax
) {}