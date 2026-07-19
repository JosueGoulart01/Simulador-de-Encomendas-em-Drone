package com.example.Simulador_de_Encomendas_em_Drone.dto;

import java.util.List;

public record DashboardResponseDTO(
    // Métricas Globais (Cards)
    long totalEntregasRealizadas,
    long totalPedidosAguardando,
    double tempoMedioMinutosPorEntrega,
    String droneMaisEficiente,
    
    // Status da Frota em Tempo Real
    long dronesTotal,
    long dronesEmVoo,
    long dronesOciosos,

    // Histórico Recente (Tabela do Dashboard)
    List<EntregaRecenteDTO> ultimasEntregas
) {}