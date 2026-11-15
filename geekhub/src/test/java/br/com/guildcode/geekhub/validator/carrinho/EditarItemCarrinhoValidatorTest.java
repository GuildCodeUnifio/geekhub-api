package br.com.guildcode.geekhub.validator.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.EditarItemCarrinhoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.carrinho.EditarItemCarrinhoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EditarItemCarrinhoValidatorTest {

    private final EditarItemCarrinhoValidator validator = new EditarItemCarrinhoValidator();

    @Test
    @DisplayName("Deve validar dados de edição quando quantidade for válida")
    void deveValidarDadosEdicaoQuandoQuantidadeForValida() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosEdicao(request));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando request de edição for nula")
    void deveLancarNegocioExceptionQuandoRequestForNula() {
        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(null));

        // Assert
        assertEquals("Dados de edição do item do carrinho são obrigatórios", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade informada for nula")
    void deveLancarNegocioExceptionQuandoQuantidadeForNula() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequest.builder()
                .quantidade(null)
                .build();

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request));

        // Assert
        assertEquals("A quantidade deve ser maior que zero", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade informada for menor ou igual a zero")
    void deveLancarNegocioExceptionQuandoQuantidadeForMenorOuIgualAZero() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequest.builder()
                .quantidade(0)
                .build();

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request));

        // Assert
        assertEquals("A quantidade deve ser maior que zero", exception.getMessage());
    }
}
