package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.DetalharAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ResumoVendedorResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DetalharAnuncioMapper {

    public DetalharAnuncioResponse toResponse(Anuncio entity, Double notaMedia, Long quantidadeAvaliacoes) {

        Usuario vendedor = entity.getVendedor();

        ResumoVendedorResponse vendedorResponse = ResumoVendedorResponse.builder()
                .id(vendedor.getId())
                .nome(vendedor.getNome())
                .imagemPerfil(vendedor.getImagemPerfil())
                .notaMedia(notaMedia)
                .quantidadeAvaliacoes(quantidadeAvaliacoes)
                .build();

        return DetalharAnuncioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .categoriaId(entity.getCategoria().getId())
                .categoriaNome(entity.getCategoria().getNome())
                .preco(entity.getPreco())
                .quantidadeDisponivel(entity.getQuantidadeDisponivel())
                .disponivel(entity.getDisponivel())
                .condicao(entity.getCondicao())
                .conservacao(entity.getConservacao())
                .imagens(new ArrayList<>(entity.getImagens()))
                .vendedor(vendedorResponse)
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
