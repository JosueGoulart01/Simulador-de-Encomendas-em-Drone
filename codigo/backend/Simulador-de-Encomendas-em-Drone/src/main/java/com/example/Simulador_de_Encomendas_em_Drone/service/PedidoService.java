package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.mapper.PedidoMapper;
import com.example.Simulador_de_Encomendas_em_Drone.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {
        Pedido pedido = pedidoMapper.toEntity(dto);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarFilaDeEspera() {
        return pedidoRepository.findByStatusOrderByPrioridadeDescDataCriacaoAsc(StatusPedido.FILA)
                .stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(pedidoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}