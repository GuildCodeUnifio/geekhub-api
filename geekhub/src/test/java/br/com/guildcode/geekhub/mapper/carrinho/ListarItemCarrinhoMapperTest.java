package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ListarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarItemCarrinhoMapperTest {

    private final ListarItemCarrinhoMapper mapper = new ListarItemCarrinhoMapper();

    @Test
    @DisplayName("Deve mapear ListarItemCarrinhoResponse corretamente")
    void deveMapearResponseCorretamente() {
        // Arrange
        ItemCarrinho entity = ItemCarrinhoFactory.get();

        // Act
        ListarItemCarrinhoResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getAnuncio().getId(), response.anuncioId());
        assertEquals(entity.getAnuncio().getVendedor().getId(), response.vendedorId());
        assertEquals(entity.getAnuncio().getNome(), response.nomeProduto());
        assertEquals("https://picsum.photos/200", response.imagem());
        assertEquals(entity.getAnuncio().getPreco(), response.precoUnitario());
        assertEquals(entity.getQuantidade(), response.quantidade());
        assertEquals(entity.getAnuncio().getQuantidadeDisponivel(), response.quantidadeDisponivel());
        assertEquals(entity.getAnuncio().getDisponivel(), response.disponivel());
        assertEquals(new BigDecimal("3700.00"), response.subtotal());
        assertEquals(entity.getCriadoEm(), response.criadoEm());
    }
}
