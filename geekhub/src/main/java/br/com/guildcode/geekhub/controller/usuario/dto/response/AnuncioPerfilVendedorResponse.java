package br.com.guildcode.geekhub.controller.usuario.dto.response;

import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AnuncioPerfilVendedorResponse(

        Long id,
        String nome,
        BigDecimal preco,
        String imagemPrincipal,
        Long categoriaId,
        String categoriaNome,
        CondicaoProduto condicao,
        ConservacaoProduto conservacao,
        Integer quantidadeDisponivel,
        LocalDateTime criadoEm
) {}
