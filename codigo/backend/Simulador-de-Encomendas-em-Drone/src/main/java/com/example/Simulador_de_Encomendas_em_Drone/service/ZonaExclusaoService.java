package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.ZonaExclusao;
import com.example.Simulador_de_Encomendas_em_Drone.dto.ZonaExclusaoRequestDTO;
import com.example.Simulador_de_Encomendas_em_Drone.dto.ZonaExclusaoResponseDTO;
import com.example.Simulador_de_Encomendas_em_Drone.mapper.ZonaExclusaoMapper;
import com.example.Simulador_de_Encomendas_em_Drone.repository.ZonaExclusaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZonaExclusaoService {

    private final ZonaExclusaoRepository zonaExclusaoRepository;
    private final ZonaExclusaoMapper zonaExclusaoMapper;

    @Transactional
    public ZonaExclusaoResponseDTO criarZona(ZonaExclusaoRequestDTO dto) {
        ZonaExclusao zona = zonaExclusaoMapper.toEntity(dto);
        ZonaExclusao zonaSalva = zonaExclusaoRepository.save(zona);
        return zonaExclusaoMapper.toResponseDTO(zonaSalva);
    }

    @Transactional(readOnly = true)
    public List<ZonaExclusaoResponseDTO> listarTodas() {
        return zonaExclusaoRepository.findAll().stream()
                .map(zonaExclusaoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletarZona(Long id) {
        if (!zonaExclusaoRepository.existsById(id)) {
            throw new IllegalArgumentException("Zona de exclusão não encontrada com o ID fornecido.");
        }
        zonaExclusaoRepository.deleteById(id);
    }
}