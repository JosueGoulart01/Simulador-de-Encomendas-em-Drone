package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;

public class CarregandoState implements DroneState {

    @Override
    public void processarTick(Drone drone) {
        // Quando termina o carregamento (neste tick), o pedido passa a estar Em Trânsito
        if (drone.getPedidosAlocados() != null) {
            drone.getPedidosAlocados().forEach(pedido -> 
                pedido.alterarStatus(StatusPedido.EM_TRANSITO)
            );
        }
        
        // Drone decola e muda o estado físico para Voo
        drone.inicializarEstado(new EmVooState());
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.CARREGANDO;
    }
}