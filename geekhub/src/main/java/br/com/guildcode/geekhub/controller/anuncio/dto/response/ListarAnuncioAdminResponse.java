package br.com.guildcode.geekhub.controller.anuncio.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ListarAnuncioAdminResponse(

        Long id,
        String nome,
        BigDecimal preco,
        Integer quantidadeDisponivel,
        Boolean disponivel,
        String categoriaNome,
        Long vendedorId,
        String vendedorNome,
        LocalDateTime criadoEm
) {}
