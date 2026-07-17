package com.example.Simulador_de_Encomendas_em_Drone.controller;

import com.example.Simulador_de_Encomendas_em_Drone.dto.ErroRespostaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura erros de validação de dados (@Valid nos DTOs).
     * Retorna HTTP 400 (Bad Request) detalhando quais campos falharam.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarErrosValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> mapaErros = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(erro -> {
            String nomeCampo = ((FieldError) erro).getField();
            String mensagemErro = erro.getDefaultMessage();
            mapaErros.put(nomeCampo, mensagemErro);
        });

        ErroRespostaDTO resposta = new ErroRespostaDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação nos Dados de Entrada",
                "Um ou mais campos contêm dados inválidos.",
                mapaErros
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    /**
     * Captura argumentos ilegais e violações de regras de negócio disparadas por nós 
     * (ex: Drone duplicado, ID inexistente, coordenadas inválidas).
     * Retorna HTTP 400 (Bad Request) com a mensagem customizada da regra.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRegrasNegocioVioladas(IllegalArgumentException ex) {
        ErroRespostaDTO resposta = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida / Regra Violada",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(resposta);
    }

    /**
     * Captura qualquer outro erro inesperado do sistema (Ex: Falha de conexão com o PostgreSQL).
     * Evita expor detalhes sensíveis de infraestrutura retornando HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErrosGenericos(Exception ex) {
        ErroRespostaDTO resposta = new ErroRespostaDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado no simulador. Por favor, tente novamente mais tarde."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
    }
}