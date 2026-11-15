package br.com.guildcode.geekhub.factory.pedido;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.pedido.EventoPedido;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static java.lang.Boolean.FALSE;

public class PedidoFactory {

    public static Pedido get() {

        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();
        LocalDateTime criadoEm = LocalDateTime.of(2026, 1, 10, 12, 0);

        ItemPedido item = ItemPedido.builder()
                .anuncioId(anuncio.getId())
                .vendedorId(anuncio.getVendedor().getId())
                .nomeProduto(anuncio.getNome())
                .imagemProduto(anuncio.getImagens().getFirst())
                .precoUnitario(anuncio.getPreco())
                .quantidade(1)
                .subtotal(anuncio.getPreco())
                .build();

        EventoPedido evento = EventoPedido.builder()
                .estado(CRIADO)
                .ocorridoEm(criadoEm)
                .build();

        return Pedido.builder()
                .id(1L)
                .comprador(comprador)
                .compradorNome(comprador.getNome())
                .compradorEmail(comprador.getEmail())
                .valorTotal(new BigDecimal("1850.00"))
                .estado(CRIADO)
                .avaliado(FALSE)
                .itens(new ArrayList<>(List.of(item)))
                .historico(new ArrayList<>(List.of(evento)))
                .criadoEm(criadoEm)
                .build();
    }
}
