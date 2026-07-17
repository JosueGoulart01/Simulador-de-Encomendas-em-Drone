package com.example.Simulador_de_Encomendas_em_Drone.controller;

import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.PedidoResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> receberPedido(@Valid @RequestBody PedidoRequestDTO dto) {
        PedidoResponseDTO response = pedidoService.criarPedido(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodosPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/fila")
    public ResponseEntity<List<PedidoResponseDTO>> verFilaDeEspera() {
        return ResponseEntity.ok(pedidoService.listarFilaDeEspera());
    }
}