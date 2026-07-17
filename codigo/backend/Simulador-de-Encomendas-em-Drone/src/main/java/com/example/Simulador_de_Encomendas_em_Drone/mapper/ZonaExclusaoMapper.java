package com.example.Simulador_de_Encomendas_em_Drone.mapper;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.ZonaExclusao;
import com.example.Simulador_de_Encomendas_em_Drone.dto.ZonaExclusaoRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.ZonaExclusaoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ZonaExclusaoMapper {

    public ZonaExclusao toEntity(ZonaExclusaoRequestDTO dto) {
        if (dto.xMin() >= dto.xMax() || dto.yMin() >= dto.yMax()) {
            throw new IllegalArgumentException("Os valores mínimos (X, Y) devem ser menores que os valores máximos.");
        }

        return ZonaExclusao.builder()
                .nome(dto.nome())
                .xMin(dto.xMin())
                .yMin(dto.yMin())
                .xMax(dto.xMax())
                .yMax(dto.yMax())
                .build();
    }

    public ZonaExclusaoResponseDTO toResponseDTO(ZonaExclusao zona) {
        return new ZonaExclusaoResponseDTO(
                zona.getId(),
                zona.getNome(),
                zona.getXMin(),
                zona.getYMin(),
                zona.getXMax(),
                zona.getYMax()
        );
    }
}