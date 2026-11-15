package br.com.guildcode.geekhub.controller.anuncio.dto.response;

import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ListarMeuAnuncioResponse(

        Long id,
        String nome,
        BigDecimal preco,
        Integer quantidadeDisponivel,
        Boolean disponivel,
        CondicaoProduto condicao,
        ConservacaoProduto conservacao,
        String categoriaNome,
        String imagemPrincipal,
        LocalDateTime criadoEm
) {}
