package com.example.Simulador_de_Encomendas_em_Drone.controller;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
import com.example.Simulador_de_Encomendas_em_Drone.service.DroneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DroneController.class)
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @MockBean
    private DroneService droneService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListAllDrones() throws Exception {
        Drone drone = Drone.builder()
                .id(1L)
                .identificador("D1")
                .statusAtual(StatusDrone.IDLE)
                .build();
        when(droneService.listarTodos()).thenReturn(List.of(drone));

        mockMvc.perform(get("/api/drones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].identificador").value("D1"));
    }

    @Test
    void shouldCreateDrone() throws Exception {
        Drone drone = Drone.builder()
                .identificador("D1")
                .capacidadeMaxKg(10.0)
                .autonomiaMaxKm(50.0)
                .build();
        when(droneService.salvar(any(Drone.class))).thenReturn(drone);

        mockMvc.perform(post("/api/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(drone)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.identificador").value("D1"));
    }
}