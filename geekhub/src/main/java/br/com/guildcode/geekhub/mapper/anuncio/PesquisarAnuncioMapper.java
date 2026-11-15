package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.stereotype.Component;

@Component
public class PesquisarAnuncioMapper {

    public ListarAnuncioResponse toResponse(Anuncio entity) {

        return ListarAnuncioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .preco(entity.getPreco())
                .imagemPrincipal(entity.obterImagemPrincipal())
                .categoriaId(entity.getCategoria().getId())
                .categoriaNome(entity.getCategoria().getNome())
                .condicao(entity.getCondicao())
                .conservacao(entity.getConservacao())
                .quantidadeDisponivel(entity.getQuantidadeDisponivel())
                .vendedorId(entity.getVendedor().getId())
                .vendedorNome(entity.getVendedor().getNome())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
