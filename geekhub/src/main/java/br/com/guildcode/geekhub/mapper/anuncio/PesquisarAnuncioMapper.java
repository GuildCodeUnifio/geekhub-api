package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class PesquisarAnuncioMapper {

    public ListarAnuncioResponse toResponse(Anuncio entity) {

        String imagemPrincipal = isNull(entity.getImagens()) || entity.getImagens().isEmpty()
                ? null
                : entity.getImagens().getFirst();

        return ListarAnuncioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .preco(entity.getPreco())
                .imagemPrincipal(imagemPrincipal)
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
