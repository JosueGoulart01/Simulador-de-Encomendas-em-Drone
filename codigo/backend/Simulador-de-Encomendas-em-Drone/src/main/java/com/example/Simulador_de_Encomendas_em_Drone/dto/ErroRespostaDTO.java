package com.example.Simulador_de_Encomendas_em_Drone.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErroRespostaDTO(
    LocalDateTime timestamp,
    int status,
    String erro,
    String mensagem,
    Map<String, String> validacoes // Armazena erros de campos específicos, se houver
) {
    // Construtor auxiliar simplificado para erros comuns
    public ErroRespostaDTO(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem, null);
    }
}