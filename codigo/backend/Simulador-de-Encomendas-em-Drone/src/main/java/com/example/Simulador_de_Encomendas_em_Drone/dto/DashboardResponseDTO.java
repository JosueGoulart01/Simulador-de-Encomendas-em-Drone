package com.example.Simulador_de_Encomendas_em_Drone.dto;

import java.util.List;

public record DashboardResponseDTO(
    // Métricas existentes
    long totalEntregasRealizadas,
    long totalPedidosAguardando,
    double tempoMedioMinutosPorEntrega,
    String droneMaisEficiente,
    long dronesTotal,
    long dronesEmVoo,
    long dronesOciosos,

    // NOVOS CAMPOS
    long totalPedidos,
    long pedidosEmTransito,        // ALOCADO + EM_TRANSITO
    double bateriaMedia,
    String droneMenorBateria,      // identificador do drone com menor %
    long dronesCarregando,
    long dronesEntregando,

    List<EntregaRecenteDTO> ultimasEntregas
) {}