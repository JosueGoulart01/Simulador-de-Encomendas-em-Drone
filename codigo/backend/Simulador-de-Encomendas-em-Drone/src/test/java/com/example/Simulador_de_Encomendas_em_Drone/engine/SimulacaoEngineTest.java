package com.example.Simulador_de_Encomendas_em_Drone.engine;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Prioridade;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import com.example.Simulador_de_Encomendas_em_Drone.repository.DroneRepository;
import com.example.Simulador_de_Encomendas_em_Drone.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimulacaoEngineTest {

    @Autowired
    private SimulacaoEngine engine;

    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        droneRepository.deleteAll();
        pedidoRepository.deleteAll();
    }

    @Test
    void shouldProcessOneTickAndMoveDrone() {
        Drone drone = Drone.builder()
                .identificador("D1")
                .capacidadeMaxKg(10.0)
                .autonomiaMaxKm(50.0)
                .bateriaAtual(100.0)
                .statusAtual(StatusDrone.IDLE)
                .posXAtual(0.0)
                .posYAtual(0.0)
                .build();
        droneRepository.save(drone);

        Pedido pedido = Pedido.builder()
                .peso(5.0)
                .posXDestino(10.0)
                .posYDestino(10.0)
                .prioridade(Prioridade.ALTA)
                .status(StatusPedido.FILA)
                .dataCriacao(LocalDateTime.now()) // <- CORRIGIDO
                .build();
        pedidoRepository.save(pedido);

        engine.processarCicloSimulacao();

        Drone updatedDrone = droneRepository.findById(drone.getId()).get();
        assertThat(updatedDrone.getStatusAtual()).isIn(StatusDrone.CARREGANDO, StatusDrone.EM_VOO);
        assertThat(updatedDrone.getPedidosAlocados()).isNotEmpty();

        Pedido updatedPedido = pedidoRepository.findAll().get(0);
        assertThat(updatedPedido.getStatus()).isIn(StatusPedido.ALOCADO, StatusPedido.EM_TRANSITO);
    }
}