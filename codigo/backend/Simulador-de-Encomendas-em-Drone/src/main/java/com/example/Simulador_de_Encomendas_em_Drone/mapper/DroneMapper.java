package com.example.Simulador_de_Encomendas_em_Drone.mapper;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DroneRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DroneResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DroneMapper {

    public Drone toEntity(DroneRequestDTO dto) {
        return Drone.builder()
                .identificador(dto.identificador())
                .capacidadeMaxKg(dto.capacidadeMaxKg())
                .autonomiaMaxKm(dto.autonomiaMaxKm())
                .bateriaAtual(100.0) // Todo drone novo começa carregado
                .statusAtual(StatusDrone.IDLE)
                .posXAtual(0.0) // Base inicial (0,0)
                .posYAtual(0.0)
                .build();
    }

    public DroneResponseDTO toResponseDTO(Drone drone) {
        return new DroneResponseDTO(
                drone.getId(),
                drone.getIdentificador(),
                drone.getCapacidadeMaxKg(),
                drone.getAutonomiaMaxKm(),
                drone.getBateriaAtual(),
                drone.getStatusAtual(),
                drone.getPosXAtual(),
                drone.getPosYAtual()
        );
    }
}