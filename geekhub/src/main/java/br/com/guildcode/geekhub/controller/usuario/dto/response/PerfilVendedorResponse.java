package br.com.guildcode.geekhub.controller.usuario.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PerfilVendedorResponse(

        Long id,
        String nome,
        String imagemPerfil,
        LocalDateTime membroDesde,
        Double notaMedia,
        Long quantidadeAvaliacoes,
        Long quantidadeProdutos,
        Long quantidadeVendas,
        List<AvaliacaoPerfilVendedorResponse> avaliacoes,
        List<AnuncioPerfilVendedorResponse> anuncios
) {}
