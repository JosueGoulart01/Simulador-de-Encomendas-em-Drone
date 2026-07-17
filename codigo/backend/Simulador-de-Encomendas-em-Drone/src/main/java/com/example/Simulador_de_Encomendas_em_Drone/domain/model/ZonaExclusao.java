package com.example.Simulador_de_Encomendas_em_Drone.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "zonas_exclusao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZonaExclusao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "x_min", nullable = false)
    private double xMin;

    @Column(name = "y_min", nullable = false)
    private double yMin;

    @Column(name = "x_max", nullable = false)
    private double xMax;

    @Column(name = "y_max", nullable = false)
    private double yMax;

    /**
     * Verifica se um ponto específico (X, Y) está dentro da zona de exclusão.
     */
    public boolean contemPonto(double x, double y) {
        return x >= this.xMin && x <= this.xMax && y >= this.yMin && y <= this.yMax;
    }

    /**
     * Verifica se um segmento de reta (rota do Drone de A até B) intercepta este obstáculo.
     * Algoritmo simplificado de interseção de linha com caixa delimitadora (Bounding Box).
     */
    public boolean interceptaRota(double x1, double y1, double x2, double y2) {
        // Se o ponto inicial ou final já estiver dentro, há interceptação
        if (contemPonto(x1, y1) || contemPonto(x2, y2)) {
            return true;
        }

        // Teste de Cohen-Sutherland simplificado ou checagem de bounding box rápida
        double minXLinha = Math.min(x1, x2);
        double maxXLinha = Math.max(x1, x2);
        double minYLinha = Math.min(y1, y2);
        double maxYLinha = Math.max(y1, y2);

        // Se a "caixa" da linha não se sobrepõe à zona de exclusão, não há colisão
        return !(maxXLinha < this.xMin || minXLinha > this.xMax || maxYLinha < this.yMin || minYLinha > this.yMax);
    }
}