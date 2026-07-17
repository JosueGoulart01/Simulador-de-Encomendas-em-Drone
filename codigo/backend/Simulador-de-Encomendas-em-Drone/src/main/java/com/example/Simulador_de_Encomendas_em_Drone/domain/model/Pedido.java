package com.example.Simulador_de_Encomendas_em_Drone.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double peso;

    @Column(name = "pos_x_destino", nullable = false)
    private double posXDestino;

    @Column(name = "pos_y_destino", nullable = false)
    private double posYDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false)
    private StatusPedido status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drone_id")
    private Drone drone; // Nullable enquanto estiver na fila aguardando alocação

    public void alterarStatus(StatusPedido novoStatus) {
        this.status = novoStatus;
    }
}