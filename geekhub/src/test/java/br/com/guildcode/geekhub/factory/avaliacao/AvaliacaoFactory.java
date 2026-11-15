package br.com.guildcode.geekhub.factory.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;

import java.time.LocalDateTime;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static java.lang.Boolean.TRUE;

public class AvaliacaoFactory {

    public static Avaliacao get() {

        Pedido pedido = PedidoFactory.get();
        pedido.setEstado(CONCLUIDO);
        pedido.setAvaliado(TRUE);

        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);
        vendedor.setNome("Vendedor GeekHub");
        vendedor.setEmail("vendedor@email.com");

        return Avaliacao.builder()
                .id(1L)
                .pedido(pedido)
                .autor(pedido.getComprador())
                .vendedor(vendedor)
                .nota(5)
                .comentario("Excelente vendedor, produto bem embalado.")
                .criadoEm(LocalDateTime.of(2026, 1, 12, 15, 30))
                .build();
    }
}
