package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.AnuncioPerfilVendedorResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.AvaliacaoPerfilVendedorResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.PerfilVendedorResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PerfilVendedorMapper {

    public PerfilVendedorResponse toResponse(
            Usuario vendedor,
            Double notaMedia,
            Long quantidadeAvaliacoes,
            Long quantidadeProdutos,
            Long quantidadeVendas,
            List<Avaliacao> avaliacoes,
            List<Anuncio> anuncios) {

        return PerfilVendedorResponse.builder()
                .id(vendedor.getId())
                .nome(vendedor.getNome())
                .imagemPerfil(vendedor.getImagemPerfil())
                .membroDesde(vendedor.getCriadoEm())
                .notaMedia(notaMedia)
                .quantidadeAvaliacoes(quantidadeAvaliacoes)
                .quantidadeProdutos(quantidadeProdutos)
                .quantidadeVendas(quantidadeVendas)
                .avaliacoes(avaliacoes.stream().map(this::toAvaliacaoResponse).toList())
                .anuncios(anuncios.stream().map(this::toAnuncioResponse).toList())
                .build();
    }

    private AvaliacaoPerfilVendedorResponse toAvaliacaoResponse(Avaliacao entity) {

        Usuario autor = entity.getAutor();

        return AvaliacaoPerfilVendedorResponse.builder()
                .id(entity.getId())
                .pedidoId(entity.getPedido().getId())
                .autorId(autor.getId())
                .autorNome(autor.getNome())
                .autorImagemPerfil(autor.getImagemPerfil())
                .nota(entity.getNota())
                .comentario(entity.getComentario())
                .criadoEm(entity.getCriadoEm())
                .build();
    }

    private AnuncioPerfilVendedorResponse toAnuncioResponse(Anuncio entity) {

        return AnuncioPerfilVendedorResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .preco(entity.getPreco())
                .imagemPrincipal(entity.obterImagemPrincipal())
                .categoriaId(entity.getCategoria().getId())
                .categoriaNome(entity.getCategoria().getNome())
                .condicao(entity.getCondicao())
                .conservacao(entity.getConservacao())
                .quantidadeDisponivel(entity.getQuantidadeDisponivel())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
