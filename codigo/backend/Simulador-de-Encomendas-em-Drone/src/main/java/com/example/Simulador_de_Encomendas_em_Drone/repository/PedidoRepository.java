package com.example.Simulador_de_Encomendas_em_Drone.repository;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Pedido;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByStatusOrderByPrioridadeDescDataCriacaoAsc(StatusPedido status);

    long countByStatus(StatusPedido status);

    // Busca o identificador do drone que tem mais registros de pedidos com status 'ENTREGUE'
    @Query(value = "SELECT d.identificador FROM pedidos p " +
                   "JOIN drones d ON p.drone_id = d.id " +
                   "WHERE p.status_pedido = 'ENTREGUE' " +
                   "GROUP BY d.identificador " +
                   "ORDER BY COUNT(p.id) DESC LIMIT 1", nativeQuery = true)
    String findDroneMaisEficiente();
}