package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarMeuAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.stereotype.Component;

@Component
public class ListarMeuAnuncioMapper {

    public ListarMeuAnuncioResponse toResponse(Anuncio entity) {

        return ListarMeuAnuncioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .preco(entity.getPreco())
                .quantidadeDisponivel(entity.getQuantidadeDisponivel())
                .disponivel(entity.getDisponivel())
                .condicao(entity.getCondicao())
                .conservacao(entity.getConservacao())
                .categoriaNome(entity.getCategoria().getNome())
                .imagemPrincipal(entity.obterImagemPrincipal())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
