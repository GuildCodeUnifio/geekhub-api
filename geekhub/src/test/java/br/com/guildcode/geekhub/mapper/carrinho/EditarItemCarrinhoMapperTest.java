package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.EditarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditarItemCarrinhoMapperTest {

    private final EditarItemCarrinhoMapper mapper = new EditarItemCarrinhoMapper();

    @Test
    @DisplayName("Deve mapear EditarItemCarrinhoResponse com subtotal recalculado")
    void deveMapearEditarItemCarrinhoResponseCorretamente() {
        // Arrange
        ItemCarrinho entity = ItemCarrinhoFactory.get();
        entity.setQuantidade(3);

        // Act
        EditarItemCarrinhoResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getAnuncio().getId(), response.anuncioId());
        assertEquals(entity.getAnuncio().getNome(), response.nomeProduto());
        assertEquals(entity.getAnuncio().getPreco(), response.precoUnitario());
        assertEquals(3, response.quantidade());
        assertEquals(new BigDecimal("5550.00"), response.subtotal());
    }
}
