package com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.ZonaExclusao;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.RoteamentoStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetaFugindoObstaculoStrategy implements RoteamentoStrategy {

    @Override
    public double[] calcularProximoPasso(double atualX, double atualY, double destX, double destY, 
                                          double velocidadePasso, List<ZonaExclusao> obstaculos) {
        
        double deltaX = destX - atualX;
        double deltaY = destY - atualY;
        double distanciaRestante = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Se já está muito perto do destino, chega exatamente nele
        if (distanciaRestante <= velocidadePasso) {
            return new double[]{destX, destY};
        }

        // Calcula o passo linear normal em linha reta (Vetor Unitário)
        double proximoX = atualX + (deltaX / distanciaRestante) * velocidadePasso;
        double proximoY = atualY + (deltaY / distanciaRestante) * velocidadePasso;

        // Valida se o passo planejado colide com alguma zona de exclusão
        for (ZonaExclusao zona : obstaculos) {
            if (zona.interceptaRota(atualX, atualY, proximoX, proximoY)) {
                // Algoritmo de desvio contornando pelas bordas externas da Bounding Box
                // Tenta contornar subindo ou desviando pela direita baseado no menor caminho
                if (atualX < zona.getXMax() && destX > zona.getXMin()) {
                    // Desvia tangenciando a borda superior de segurança (Y Máximo + margem)
                    return new double[]{atualX, zona.getYMax() + 0.5};
                } else {
                    // Desvia tangenciando a borda lateral direita de segurança (X Máximo + margem)
                    return new double[]{zona.getXMax() + 0.5, atualY};
                }
            }
        }

        return new double[]{proximoX, proximoY};
    }
}