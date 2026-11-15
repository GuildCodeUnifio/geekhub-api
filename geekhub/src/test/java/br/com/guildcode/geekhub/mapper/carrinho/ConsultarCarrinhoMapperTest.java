package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ConsultarCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ItemCarrinhoDetalheResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConsultarCarrinhoMapperTest {

    private final ConsultarCarrinhoMapper mapper = new ConsultarCarrinhoMapper();

    @Test
    @DisplayName("Deve mapear item do carrinho utilizando imagem principal e subtotal da entidade")
    void deveMapearItemDoCarrinhoCorretamente() {
        // Arrange
        ItemCarrinho entity = ItemCarrinhoFactory.get();

        // Act
        ItemCarrinhoDetalheResponse response = mapper.toItemResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getAnuncio().getId(), response.anuncioId());
        assertEquals(entity.getAnuncio().getVendedor().getId(), response.vendedorId());
        assertEquals(entity.getAnuncio().getNome(), response.nome());
        assertEquals("https://picsum.photos/200", response.imagem());
        assertEquals(entity.getAnuncio().getPreco(), response.precoUnitario());
        assertEquals(entity.getQuantidade(), response.quantidade());
        assertEquals(entity.getAnuncio().getQuantidadeDisponivel(), response.quantidadeDisponivel());
        assertEquals(entity.getAnuncio().getDisponivel(), response.disponivel());
        assertEquals(new BigDecimal("3700.00"), response.subtotal());
    }

    @Test
    @DisplayName("Deve mapear ConsultarCarrinhoResponse corretamente")
    void deveMapearConsultarCarrinhoResponseCorretamente() {
        // Arrange
        ItemCarrinhoDetalheResponse item = mapper.toItemResponse(ItemCarrinhoFactory.get());
        List<ItemCarrinhoDetalheResponse> itens = List.of(item);
        BigDecimal total = new BigDecimal("3700.00");

        // Act
        ConsultarCarrinhoResponse response = mapper.toResponse(1L, itens, total);

        // Assert
        assertEquals(1L, response.compradorId());
        assertEquals(itens, response.itens());
        assertEquals(total, response.total());
    }
}
