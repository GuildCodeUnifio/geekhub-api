package br.com.guildcode.geekhub.validator.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.avaliacao.EditarAvaliacaoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EditarAvaliacaoValidatorTest {

    private final EditarAvaliacaoValidator validator = new EditarAvaliacaoValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando dados de edição da avaliação forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {
        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(null)
        );

        assertEquals("Dados da avaliação são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando nota na edição for nula")
    void deveLancarExcecaoQuandoNotaNula() {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequest.builder()
                .nota(null)
                .comentario("Comentário")
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando nota na edição for menor que 1")
    void deveLancarExcecaoQuandoNotaMenorQueUm() {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequest.builder()
                .nota(0)
                .comentario("Comentário")
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando nota na edição for maior que 5")
    void deveLancarExcecaoQuandoNotaMaiorQueCinco() {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequest.builder()
                .nota(6)
                .comentario("Comentário")
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando dados de edição da avaliação forem válidos")
    void deveValidarDadosEdicaoComSucesso() {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosEdicao(request));
    }
}
