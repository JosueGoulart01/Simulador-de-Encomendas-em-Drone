package com.example.Simulador_de_Encomendas_em_Drone.repository;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    long countByStatus(StatusPedido status);
    
    List<Pedido> findByStatusOrderByPrioridadeDescDataCriacaoAsc(StatusPedido status);

    // Busca os últimos 10 pedidos entregues para o histórico do painel
    List<Pedido> findTop10ByStatusOrderByIdDesc(StatusPedido status);

    // Query customizada para achar o drone que mais vezes aparece em pedidos concluídos
    @Query("SELECT p.drone.identificador FROM Pedido p WHERE p.status = 'ENTREGUE' GROUP BY p.drone.identificador ORDER BY COUNT(p) DESC LIMIT 1")
    String findDroneMaisEficiente();
}