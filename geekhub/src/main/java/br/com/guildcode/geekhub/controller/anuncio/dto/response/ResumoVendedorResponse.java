package br.com.guildcode.geekhub.controller.anuncio.dto.response;

import lombok.Builder;

@Builder
public record ResumoVendedorResponse(

        Long id,
        String nome,
        String imagemPerfil,
        Double notaMedia,
        Long quantidadeAvaliacoes
) {}
