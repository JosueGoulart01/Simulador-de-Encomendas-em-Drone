package com.example.Simulador_de_Encomendas_em_Drone.repository;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
    }

    @Test
    void shouldCountByStatus() {
        Pedido p1 = Pedido.builder()
                .peso(1.0)
                .posXDestino(1.0)
                .posYDestino(1.0)
                .prioridade(Prioridade.MEDIA)
                .status(StatusPedido.ENTREGUE)
                .dataCriacao(LocalDateTime.now())
                .build();
        Pedido p2 = Pedido.builder()
                .peso(2.0)
                .posXDestino(2.0)
                .posYDestino(2.0)
                .prioridade(Prioridade.ALTA)
                .status(StatusPedido.FILA)
                .dataCriacao(LocalDateTime.now())
                .build();
        pedidoRepository.saveAll(List.of(p1, p2));

        long entregues = pedidoRepository.countByStatus(StatusPedido.ENTREGUE);
        long naFila = pedidoRepository.countByStatus(StatusPedido.FILA);

        assertThat(entregues).isEqualTo(1);
        assertThat(naFila).isEqualTo(1);
    }

    @Test
    void shouldFindByStatusOrderedByPriority() {
        LocalDateTime now = LocalDateTime.now();
        
        Pedido p1 = Pedido.builder()
                .peso(1.0)
                .posXDestino(1.0)
                .posYDestino(1.0)
                .prioridade(Prioridade.BAIXA)
                .status(StatusPedido.FILA)
                .dataCriacao(now.minusMinutes(10))
                .build();
        Pedido p2 = Pedido.builder()
                .peso(2.0)
                .posXDestino(2.0)
                .posYDestino(2.0)
                .prioridade(Prioridade.ALTA)
                .status(StatusPedido.FILA)
                .dataCriacao(now)
                .build();
        pedidoRepository.saveAll(List.of(p1, p2));

        List<Pedido> fila = pedidoRepository.findByStatusOrderByPrioridadeDescDataCriacaoAsc(StatusPedido.FILA);

        assertThat(fila).hasSize(2);
        assertThat(fila.get(0).getPrioridade()).isEqualTo(Prioridade.ALTA);
        assertThat(fila.get(1).getPrioridade()).isEqualTo(Prioridade.BAIXA);
    }
}