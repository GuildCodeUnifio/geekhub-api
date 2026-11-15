package br.com.guildcode.geekhub.controller.anuncio.dto.response;

import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ListarAnuncioResponse(

        Long id,
        String nome,
        BigDecimal preco,
        String imagemPrincipal,
        Long categoriaId,
        String categoriaNome,
        CondicaoProduto condicao,
        ConservacaoProduto conservacao,
        Integer quantidadeDisponivel,
        Long vendedorId,
        String vendedorNome,
        LocalDateTime criadoEm
) {}
