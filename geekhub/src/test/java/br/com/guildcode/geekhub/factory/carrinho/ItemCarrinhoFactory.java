package br.com.guildcode.geekhub.factory.carrinho;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;

import java.time.LocalDateTime;

public class ItemCarrinhoFactory {

    public static ItemCarrinho get() {

        return ItemCarrinho.builder()
                .id(1L)
                .comprador(UsuarioFactory.get())
                .anuncio(AnuncioFactory.get())
                .quantidade(2)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
    }
}
