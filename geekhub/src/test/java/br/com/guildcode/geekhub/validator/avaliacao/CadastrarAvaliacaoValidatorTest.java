package br.com.guildcode.geekhub.validator.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.avaliacao.CadastrarAvaliacaoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CadastrarAvaliacaoValidatorTest {

    private final CadastrarAvaliacaoValidator validator = new CadastrarAvaliacaoValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando dados da avaliação forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {
        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(null)
        );

        assertEquals("Dados da avaliação são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando pedidoId for nulo")
    void deveLancarExcecaoQuandoPedidoIdNulo() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequest.builder()
                .pedidoId(null)
                .nota(5)
                .comentario("Excelente!")
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("O pedido da avaliação é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando nota for nula")
    void deveLancarExcecaoQuandoNotaNula() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequest.builder()
                .pedidoId(3L)
                .nota(null)
                .comentario("Sem nota")
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando nota for menor que 1")
    void deveLancarExcecaoQuandoNotaMenorQueUm() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequest.builder()
                .pedidoId(3L)
                .nota(0)
                .comentario("Nota inválida")
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando nota for maior que 5")
    void deveLancarExcecaoQuandoNotaMaiorQueCinco() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequest.builder()
                .pedidoId(3L)
                .nota(6)
                .comentario("Nota inválida")
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando dados da avaliação forem válidos nos limites de nota 1 e 5")
    void deveValidarDadosCadastroComSucesso() {
        // Arrange
        CadastrarAvaliacaoRequest requestNotaMinima = CadastrarAvaliacaoRequest.builder()
                .pedidoId(3L)
                .nota(1)
                .comentario(null)
                .build();
        CadastrarAvaliacaoRequest requestValida = CadastrarAvaliacaoRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosCadastro(requestNotaMinima));
        assertDoesNotThrow(() -> validator.validarDadosCadastro(requestValida));
    }
}
