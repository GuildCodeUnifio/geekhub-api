package br.com.guildcode.geekhub.factory.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.USADO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.BOM;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class EditarAnuncioRequestFactory {

    public static EditarAnuncioRequest get() {

        return EditarAnuncioRequest.builder()
                .nome("Estátua Kakashi Hatake 18cm")
                .descricao("Peça em PVC exposta apenas em cristaleira, acompanha caixa.")
                .categoriaId(1L)
                .preco(new BigDecimal("329.90"))
                .quantidadeDisponivel(2)
                .disponivel(TRUE)
                .condicao(USADO)
                .conservacao(BOM)
                .imagens(List.of("https://picsum.photos/200"))
                .build();
    }

    public static EditarAnuncioRequest semEstoque() {

        return EditarAnuncioRequest.builder()
                .nome("Estátua Kakashi Hatake 18cm")
                .descricao("Peça esgotada.")
                .categoriaId(1L)
                .preco(new BigDecimal("329.90"))
                .quantidadeDisponivel(0)
                .disponivel(FALSE)
                .condicao(USADO)
                .conservacao(BOM)
                .imagens(List.of("https://picsum.photos/200"))
                .build();
    }
}
