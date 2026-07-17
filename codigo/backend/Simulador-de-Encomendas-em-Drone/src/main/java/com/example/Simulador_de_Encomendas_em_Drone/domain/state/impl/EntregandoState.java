package com.example.Simulador_de_Encomendas_em_Drone.domain.state.impl;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.state.DroneState;

public class EntregandoState implements DroneState {

    @Override
    public void processarTick(Drone drone) {
        if (drone.getPedidosAlocados() != null && !drone.getPedidosAlocados().isEmpty()) {
            // Remove o pacote que acabou de ser entregue da lista
            Pedido pedidoEntregue = drone.getPedidosAlocados().remove(0);
            pedidoEntregue.alterarStatus(StatusPedido.ENTREGUE);
        }

        // Se ainda restam pacotes na mochila na mesma viagem, continua voando. Caso contrário, retorna à base (0,0)
        if (drone.getPedidosAlocados() != null && !drone.getPedidosAlocados().isEmpty()) {
            drone.inicializarEstado(new EmVooState());
        } else {
            drone.inicializarEstado(new RetornandoState());
        }
    }

    @Override
    public StatusDrone getStatus() {
        return StatusDrone.ENTREGANDO;
    }
}