package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarrinhoNaoVazioValidatorServiceTest {

    private final CarrinhoNaoVazioValidatorService validator = new CarrinhoNaoVazioValidatorService();

    @Test
    @DisplayName("Deve validar carrinho quando houver pelo menos um item")
    void deveValidarQuandoHouverItensNoCarrinho() {
        // Arrange
        List<ItemCarrinho> itens = List.of(ItemCarrinhoFactory.get());

        // Act & Assert
        assertDoesNotThrow(() -> validator.validar(itens));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando lista de itens do carrinho estiver vazia")
    void deveLancarNegocioExceptionQuandoCarrinhoEstiverVazio() {
        // Arrange
        List<ItemCarrinho> itens = Collections.emptyList();

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validar(itens));

        // Assert
        assertEquals("O carrinho de compras está vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando lista de itens do carrinho for nula")
    void deveLancarNegocioExceptionQuandoListaItensForNula() {
        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validar(null));

        // Assert
        assertEquals("O carrinho de compras está vazio", exception.getMessage());
    }
}
