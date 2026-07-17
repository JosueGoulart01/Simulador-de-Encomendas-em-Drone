package com.example.Simulador_de_Encomendas_em_Drone.controller;

import com.example.Simulador_de_Encomendas_em_Drone.dto.DroneRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.DroneResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.service.DroneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
@RequiredArgsConstructor
public class DroneController {

    private final DroneService droneService;

    @PostMapping
    public ResponseEntity<DroneResponseDTO> criarDrone(@Valid @RequestBody DroneRequestDTO dto) {
        DroneResponseDTO response = droneService.cadastrarDrone(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DroneResponseDTO>> listarDrones() {
        return ResponseEntity.ok(droneService.listarTodosDrones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroneResponseDTO> buscarDrone(@PathVariable Long id) {
        return ResponseEntity.ok(droneService.buscarPorId(id));
    }
}