package com.example.Simulador_de_Encomendas_em_Drone.repository;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.ZonaExclusao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaExclusaoRepository extends JpaRepository<ZonaExclusao, Long> {
}