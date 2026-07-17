package com.example.Simulador_de_Encomendas_em_Drone.dto;

public record DashboardResponseDTO(
    long totalEntregasRealizadas,
    long totalPedidosAguardando,
    double tempoMedioMinutosPorEntrega,
    String droneMaisEficiente // Drone com o maior número de entregas concluídas
) {}