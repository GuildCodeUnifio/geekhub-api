package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.AdicionarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdicionarItemCarrinhoMapperTest {

    private final AdicionarItemCarrinhoMapper mapper = new AdicionarItemCarrinhoMapper();

    @Test
    @DisplayName("Deve mapear entidade ItemCarrinho corretamente")
    void deveMapearEntidadeCorretamente() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();
        LocalDateTime agora = LocalDateTime.of(2026, 2, 1, 14, 0);

        // Act
        ItemCarrinho entity = mapper.toEntity(comprador, anuncio, 2, agora);

        // Assert
        assertEquals(comprador, entity.getComprador());
        assertEquals(anuncio, entity.getAnuncio());
        assertEquals(2, entity.getQuantidade());
        assertEquals(agora, entity.getCriadoEm());
    }

    @Test
    @DisplayName("Deve mapear AdicionarItemCarrinhoResponse com subtotal calculado corretamente")
    void deveMapearResponseComSubtotalCalculadoCorretamente() {
        // Arrange
        ItemCarrinho entity = ItemCarrinhoFactory.get();

        // Act
        AdicionarItemCarrinhoResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getAnuncio().getId(), response.anuncioId());
        assertEquals(entity.getAnuncio().getNome(), response.nomeProduto());
        assertEquals(entity.getAnuncio().getPreco(), response.precoUnitario());
        assertEquals(entity.getQuantidade(), response.quantidade());
        assertEquals(new BigDecimal("3700.00"), response.subtotal());
        assertEquals(entity.getCriadoEm(), response.criadoEm());
    }
}
