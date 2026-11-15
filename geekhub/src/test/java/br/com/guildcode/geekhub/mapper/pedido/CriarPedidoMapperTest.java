package br.com.guildcode.geekhub.mapper.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriarPedidoMapperTest {

    private final CriarPedidoMapper mapper = new CriarPedidoMapper();

    @Test
    @DisplayName("Deve converter ItemCarrinho em snapshot imutável ItemPedido")
    void deveConverterItemCarrinhoEmItemPedido() {
        // Arrange
        ItemCarrinho itemCarrinho = ItemCarrinhoFactory.get();

        // Act
        ItemPedido itemPedido = mapper.toItemPedido(itemCarrinho);

        // Assert
        assertEquals(itemCarrinho.getAnuncio().getId(), itemPedido.getAnuncioId());
        assertEquals(itemCarrinho.getAnuncio().getVendedor().getId(), itemPedido.getVendedorId());
        assertEquals(itemCarrinho.getAnuncio().getNome(), itemPedido.getNomeProduto());
        assertEquals("https://picsum.photos/200", itemPedido.getImagemProduto());
        assertEquals(itemCarrinho.getAnuncio().getPreco(), itemPedido.getPrecoUnitario());
        assertEquals(itemCarrinho.getQuantidade(), itemPedido.getQuantidade());
        assertEquals(new BigDecimal("3700.00"), itemPedido.getSubtotal());
    }

    @Test
    @DisplayName("Deve mapear entidade Pedido de forma declarativa a partir do comprador e itens")
    void deveMapearEntidadePedidoDeclarativamente() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemPedido item = mapper.toItemPedido(ItemCarrinhoFactory.get());
        LocalDateTime agora = LocalDateTime.of(2026, 11, 15, 20, 0);

        // Act
        Pedido pedido = mapper.toEntity(comprador, List.of(item), agora);

        // Assert
        assertEquals(comprador, pedido.getComprador());
        assertEquals(comprador.getNome(), pedido.getCompradorNome());
        assertEquals(comprador.getEmail(), pedido.getCompradorEmail());
        assertEquals(List.of(item), pedido.getItens());
        assertEquals(FALSE, pedido.getAvaliado());
        assertEquals(agora, pedido.getCriadoEm());
        assertNull(pedido.getValorTotal());
        assertTrue(pedido.getHistorico().isEmpty());
    }

    @Test
    @DisplayName("Deve mapear CriarPedidoResponse completo a partir de Pedido")
    void deveMapearCriarPedidoResponseCorretamente() {
        // Arrange
        Pedido entity = PedidoFactory.get();

        // Act
        CriarPedidoResponse response = mapper.toResponse(entity);

        // Assert
        assertEquals(entity.getId(), response.id());
        assertEquals(entity.getComprador().getId(), response.compradorId());
        assertEquals(entity.getCompradorNome(), response.compradorNome());
        assertEquals(entity.getCompradorEmail(), response.compradorEmail());
        assertEquals(entity.getValorTotal(), response.valorTotal());
        assertEquals(entity.getEstado(), response.estado());
        assertEquals(entity.getAvaliado(), response.avaliado());
        assertEquals(entity.getCriadoEm(), response.criadoEm());
        assertEquals(1, response.itens().size());
        assertEquals(entity.getItens().getFirst().getNomeProduto(), response.itens().getFirst().nomeProduto());
        assertEquals(1, response.historico().size());
        assertEquals(CRIADO, response.historico().getFirst().estado());
    }
}
