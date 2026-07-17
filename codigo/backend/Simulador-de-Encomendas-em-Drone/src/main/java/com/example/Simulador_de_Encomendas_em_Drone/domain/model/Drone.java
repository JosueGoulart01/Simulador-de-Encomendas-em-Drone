package com.example.Simulador_de_Encomendas_em_Drone.domain.model;

import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl.IdleState; // Estado padrão inicial
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificador;

    @Column(name = "capacidade_max_kg", nullable = false)
    private double capacidadeMaxKg;

    @Column(name = "autonomia_max_km", nullable = false)
    private double autonomiaMaxKm;

    @Column(name = "bateria_atual", nullable = false)
    private double bateriaAtual;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_atual", nullable = false)
    private StatusDrone statusAtual;

    @Column(name = "pos_x_atual", nullable = false)
    private double posXAtual;

    @Column(name = "pos_y_atual", nullable = false)
    private double posYAtual;

    @Transient // Não persistido diretamente no banco, resolvido em tempo de execução
    private DroneState estadoInstancia;

    /**
     * Sincroniza a instância do padrão State baseada no Enum salvo no banco.
     */
    public void inicializarEstado(DroneState estado) {
        this.estadoInstancia = estado;
        this.statusAtual = estado.getStatus();
    }

    public void executarAcaoSimulacao() {
        if (this.estadoInstancia != null) {
            this.estadoInstancia.processarTick(this);
        }
    }

    public void atualizarPosicao(double x, double y) {
        this.posXAtual = x;
        this.posYAtual = y;
    }

    public void consumirBateria(double quantidade) {
        this.bateriaAtual = Math.max(0, this.bateriaAtual - quantidade);
    }

    public void recarregar() {
        this.bateriaAtual = 100.0;
    }
}