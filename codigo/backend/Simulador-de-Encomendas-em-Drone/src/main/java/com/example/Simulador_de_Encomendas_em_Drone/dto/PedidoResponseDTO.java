package com.example.Simulador_de_Encomendas_em_Drone.dto;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import java.time.LocalDateTime;

public record PedidoResponseDTO(
    Long id,
    double peso,
    double posXDestino,
    double posYDestino,
    Prioridade prioridade,
    StatusPedido status,
    LocalDateTime dataCriacao,
    Long droneId
) {}