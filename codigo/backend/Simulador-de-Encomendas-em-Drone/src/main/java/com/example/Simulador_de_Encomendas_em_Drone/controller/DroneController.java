package com.example.Simulador_de_Encomendas_em_Drone.controller;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
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

    @GetMapping
    public ResponseEntity<List<Drone>> listarTodos() {
        return ResponseEntity.ok(droneService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Drone> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(droneService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Drone> criar(@Valid @RequestBody Drone drone) {
        return ResponseEntity.status(HttpStatus.CREATED).body(droneService.salvar(drone));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Drone> atualizar(@PathVariable Long id, @Valid @RequestBody Drone droneAtualizado) {
        return ResponseEntity.ok(droneService.atualizar(id, droneAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        droneService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}