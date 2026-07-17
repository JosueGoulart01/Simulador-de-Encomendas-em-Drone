package com.example.Simulador_de_Encomendas_em_Drone.dto;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoRequestDTO(
    @Positive(message = "O peso do pacote deve ser maior que zero")
    double peso,

    @NotNull(message = "A coordenada X de destino é obrigatória")
    double posXDestino,

    @NotNull(message = "A coordenada Y de destino é obrigatória")
    double posYDestino,

    @NotNull(message = "A prioridade do pedido é obrigatória")
    Prioridade prioridade
) {}