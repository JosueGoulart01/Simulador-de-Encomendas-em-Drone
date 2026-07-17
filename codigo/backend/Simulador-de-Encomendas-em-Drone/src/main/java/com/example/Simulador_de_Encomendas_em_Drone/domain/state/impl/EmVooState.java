package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.RoteamentoStrategy;
import com.example.Simulador_de_Encomendas_em_Drone.domain.strategy.impl.RetaFugindoObstaculoStrategy;

public class EmVooState implements DroneState {

    // Usando instanciação direta ou lookup para simplificar o desacoplamento de beans nos estados transient
    private final RoteamentoStrategy roteamentoStrategy = new RetaFugindoObstaculoStrategy();
    private static final double VELOCIDADE_DRONE = 2.0; // Unidades de grade por tick

    @Override
    public void processarTick(Drone drone) {
        if (drone.getPedidosAlocados() == null || drone.getPedidosAlocados().isEmpty()) {
            drone.inicializarEstado(new RetornandoState());
            return;
        }

        // Pega o primeiro pedido da rota de entregas agendadas
        Pedido pedidoAtual = drone.getPedidosAlocados().get(0);
        
        // Calcula o próximo passo geométrico desviando de obstáculos (Zonas de exclusão vazias por padrão aqui)
        double[] proximaPosicao = roteamentoStrategy.calcularProximoPasso(
                drone.getPosXAtual(), drone.getPosYAtual(),
                pedidoAtual.getPosXDestino(), pedidoAtual.getPosYDestino(),
                VELOCIDADE_DRONE, java.util.Collections.emptyList()
        );

        // Atualiza a posição física do Drone
        drone.atualizarPosicao(proximaPosicao[0], proximaPosicao[1]);

        // Simula o consumo de bateria proporcional ao deslocamento e peso extra
        double fatorCarga = 1.0 + (pedidoAtual.getPeso() / drone.getCapacidadeMaxKg());
        drone.consumirBateria(VELOCIDADE_DRONE * 0.5 * fatorCarga);

        // Chegou exatamente nas coordenadas do destino do pacote?
        if (drone.getPosXAtual() == pedidoAtual.getPosXDestino() && drone.getPosYAtual() == pedidoAtual.getPosYDestino()) {
            drone.inicializarEstado(new EntregandoState());
        }
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.EM_VOO;
    }
}