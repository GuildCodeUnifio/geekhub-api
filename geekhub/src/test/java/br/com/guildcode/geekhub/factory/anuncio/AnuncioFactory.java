package br.com.guildcode.geekhub.factory.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
import static java.lang.Boolean.TRUE;

public class AnuncioFactory {

    public static Anuncio get() {

        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);
        vendedor.setNome("Vendedor GeekHub");
        vendedor.setEmail("vendedor@email.com");

        return Anuncio.builder()
                .id(1L)
                .nome("Action figure Naruto 1ª edição")
                .descricao("Peça original de colecionador lacrada na caixa.")
                .preco(new BigDecimal("1850.00"))
                .quantidadeDisponivel(3)
                .disponivel(TRUE)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .categoria(CategoriaFactory.get())
                .vendedor(vendedor)
                .imagens(new ArrayList<>(List.of("https://picsum.photos/200")))
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
    }
}
