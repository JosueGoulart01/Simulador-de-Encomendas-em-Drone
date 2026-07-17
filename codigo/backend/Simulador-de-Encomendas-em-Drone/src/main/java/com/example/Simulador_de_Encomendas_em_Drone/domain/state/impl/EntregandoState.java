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
            // Remove o primeiro pedido da lista do drone (o que acabou de chegar ao destino)
            Pedido pedidoEntregue = drone.getPedidosAlocados().remove(0);
            
            // SÓ AQUI o status vai para concluído no banco de dados!
            pedidoEntregue.alterarStatus(StatusPedido.ENTREGUE);
            pedidoEntregue.setDrone(null); // Quebra o vínculo com o drone
        }

        // Se o drone ainda tiver mais pedidos na lista (entregas em lote), continua voando. 
        // Se não, inicia o trajeto de retorno para a base central.
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