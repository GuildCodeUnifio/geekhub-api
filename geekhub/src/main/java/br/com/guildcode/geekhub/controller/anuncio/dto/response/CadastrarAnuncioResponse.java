package br.com.guildcode.geekhub.controller.anuncio.dto.response;

import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CadastrarAnuncioResponse(

        Long id,
        String nome,
        String descricao,
        Long categoriaId,
        String categoriaNome,
        BigDecimal preco,
        Integer quantidadeDisponivel,
        Boolean disponivel,
        CondicaoProduto condicao,
        ConservacaoProduto conservacao,
        List<String> imagens,
        Long vendedorId,
        LocalDateTime criadoEm
) {}
