package com.example.Simulador_de_Encomendas_em_Drone.mapper;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoResponseDTO;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class PedidoMapper {

    public Pedido toEntity(PedidoRequestDTO dto) {
        return Pedido.builder()
                .peso(dto.peso())
                .posXDestino(dto.posXDestino())
                .posYDestino(dto.posYDestino())
                .prioridade(dto.prioridade())
                .status(StatusPedido.FILA) // Todo pedido novo entra na fila
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        Long droneId = (pedido.getDrone() != null) ? pedido.getDrone().getId() : null;
        
        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getPeso(),
                pedido.getPosXDestino(),
                pedido.getPosYDestino(),
                pedido.getPrioridade(),
                pedido.getStatus(),
                pedido.getDataCriacao(),
                droneId
        );
    }
}