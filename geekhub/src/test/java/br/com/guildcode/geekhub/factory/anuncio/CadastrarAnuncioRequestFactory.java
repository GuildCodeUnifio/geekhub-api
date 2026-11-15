package br.com.guildcode.geekhub.factory.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;

public class CadastrarAnuncioRequestFactory {

    public static CadastrarAnuncioRequest get() {

        return CadastrarAnuncioRequest.builder()
                .nome("Action figure Naruto 1ª edição")
                .descricao("Peça original de colecionador lacrada na caixa com nota fiscal.")
                .categoriaId(1L)
                .preco(new BigDecimal("1850.00"))
                .quantidadeDisponivel(3)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of("https://picsum.photos/200"))
                .build();
    }
}
