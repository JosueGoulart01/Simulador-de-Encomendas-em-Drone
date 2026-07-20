package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.mapper.PedidoMapper;
import com.example.Simulador_de_Encomendas_em_Drone.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void shouldCreatePedido() {
        PedidoRequestDTO dto = new PedidoRequestDTO(5.0, 10.0, 20.0, Prioridade.ALTA);
        Pedido pedido = Pedido.builder()
                .peso(5.0)
                .posXDestino(10.0)
                .posYDestino(20.0)
                .prioridade(Prioridade.ALTA)
                .status(StatusPedido.FILA)
                .dataCriacao(LocalDateTime.now())
                .build();
        pedido.setId(1L);

        when(pedidoMapper.toEntity(dto)).thenReturn(pedido);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(
                new PedidoResponseDTO(1L, 5.0, 10.0, 20.0, Prioridade.ALTA, StatusPedido.FILA, LocalDateTime.now(), null)
        );

        PedidoResponseDTO response = pedidoService.criarPedido(dto);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.peso()).isEqualTo(5.0);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void shouldListFila() {
        Pedido p = Pedido.builder().id(1L).peso(1.0).build();
        when(pedidoRepository.findByStatusOrderByPrioridadeDescDataCriacaoAsc(StatusPedido.FILA))
                .thenReturn(List.of(p));
        when(pedidoMapper.toResponseDTO(any(Pedido.class))).thenReturn(
                new PedidoResponseDTO(1L, 1.0, 0.0, 0.0, Prioridade.BAIXA, StatusPedido.FILA, LocalDateTime.now(), null)
        );

        List<PedidoResponseDTO> fila = pedidoService.listarFilaDeEspera();

        assertThat(fila).hasSize(1);
    }
}