package com.example.Simulador_de_Encomendas_em_Drone.controller;

import com.example.Simulador_de_Encomendas_em_Drone.dto.ZonaExclusaoRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.ZonaExclusaoResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.service.ZonaExclusaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zonas-exclusao")
@RequiredArgsConstructor
public class ZonaExclusaoController {

    private final ZonaExclusaoService zonaExclusaoService;

    @PostMapping
    public ResponseEntity<ZonaExclusaoResponseDTO> criarZona(@Valid @RequestBody ZonaExclusaoRequestDTO dto) {
        ZonaExclusaoResponseDTO response = zonaExclusaoService.criarZona(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ZonaExclusaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(zonaExclusaoService.listarTodas());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerZona(@PathVariable Long id) {
        zonaExclusaoService.deletarZona(id);
        return ResponseEntity.noContent().build();
    }
}