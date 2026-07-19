package com.example.Simulador_de_Encomendas_em_Drone.service;

import com.example.Simulador_de_Encomendas_em_Drone.domain.model.Drone;
import com.example.Simulador_de_Encomendas_em_Drone.domain.model.StatusDrone;
// Using standard runtime exceptions instead of a missing custom BusinessException
import com.example.Simulador_de_Encomendas_em_Drone.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DroneService {

    private final DroneRepository droneRepository;

    public List<Drone> listarTodos() {
        return droneRepository.findAll();
    }

    public Drone buscarPorId(Long id) {
        return droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drone com ID " + id + " não encontrado."));
    }

    @Transactional
    public Drone salvar(Drone drone) {
        // Todo drone novo começa na Base Central (0,0), com bateria cheia e ocioso
        drone.setPosXAtual(0.0);
        drone.setPosYAtual(0.0);
        drone.setBateriaAtual(100.0);
        drone.setStatusAtual(StatusDrone.IDLE);
        return droneRepository.save(drone);
    }

    @Transactional
    public Drone atualizar(Long id, Drone dadosAtualizados) {
        Drone droneExistente = buscarPorId(id);

        // Impede a edição de configurações críticas se o drone estiver em missão física
        if (droneExistente.getStatusAtual() != StatusDrone.IDLE) {
            throw new RuntimeException("Não é possível atualizar um drone que não esteja em estado IDLE (Ocioso). Status atual: " + droneExistente.getStatusAtual());
        }

        // Atualiza estritamente os parâmetros estruturais
        droneExistente.setIdentificador(dadosAtualizados.getIdentificador());
        droneExistente.setCapacidadeMaxKg(dadosAtualizados.getCapacidadeMaxKg());
        droneExistente.setAutonomiaMaxKm(dadosAtualizados.getAutonomiaMaxKm());

        return droneRepository.save(droneExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Drone drone = buscarPorId(id);

        // Impede a deleção de drones em pleno vôo para evitar inconsistência de dados
        if (drone.getStatusAtual() != StatusDrone.IDLE) {
            throw new RuntimeException("Não é possível remover um drone em missão ativa! Status atual: " + drone.getStatusAtual());
        }

        droneRepository.delete(drone);
    }
}