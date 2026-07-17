package com.example.Simulador_de_Encomendas_em_Drone.domain.strategy;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.ZonaExclusao;
import java.util.List;

public interface RoteamentoStrategy {
    /**
     * Calcula o próximo ponto (X, Y) na malha se deslocando em direção ao destino, 
     * desviando dos obstáculos presentes na lista.
     */
    double[] calcularProximoPasso(double atualX, double atualY, double destX, double destY, 
                                  double velocidadePasso, List<ZonaExclusao> obstaculos);
}