package br.com.guildcode.geekhub.validator.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.carrinho.AdicionarItemCarrinhoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdicionarItemCarrinhoValidatorTest {

    private final AdicionarItemCarrinhoValidator validator = new AdicionarItemCarrinhoValidator();

    @Test
    @DisplayName("Deve validar dados de adição quando request for válida")
    void deveValidarDadosAdicaoQuandoRequestForValida() {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosAdicao(request));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando request for nula")
    void deveLancarNegocioExceptionQuandoRequestForNula() {
        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosAdicao(null));

        // Assert
        assertEquals("Dados do item do carrinho são obrigatórios", exception.getMessage());
    }
}
