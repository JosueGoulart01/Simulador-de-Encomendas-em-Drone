package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DroneRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DroneResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.mapper.DroneMapper;
import lombok.RequiredArgsConstructor;
import com.example.Simulador_de_Encomendas_em_Drone.repository.DroneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Lombok gera construtor para injeção via Spring
public class DroneService {

    private final DroneRepository droneRepository;
    private final DroneMapper droneMapper;

    @Transactional
    public DroneResponseDTO cadastrarDrone(DroneRequestDTO dto) {
        if (droneRepository.existsByIdentificador(dto.identificador())) {
            throw new IllegalArgumentException("Já existe um drone cadastrado com este identificador.");
        }
        Drone drone = droneMapper.toEntity(dto);
        Drone droneSalvo = droneRepository.save(drone);
        return droneMapper.toResponseDTO(droneSalvo);
    }

    @Transactional(readOnly = true)
    public List<DroneResponseDTO> listarTodosDrones() {
        return droneRepository.findAll().stream()
                .map(droneMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DroneResponseDTO buscarPorId(Long id) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Drone não encontrado com o ID fornecido."));
        return droneMapper.toResponseDTO(drone);
    }
}