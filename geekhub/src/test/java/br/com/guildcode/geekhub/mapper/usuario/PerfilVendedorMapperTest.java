package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.PerfilVendedorResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerfilVendedorMapperTest {

    private final PerfilVendedorMapper mapper = new PerfilVendedorMapper();

    @Test
    @DisplayName("Deve mapear perfil completo do vendedor com avaliações e anúncios disponíveis")
    void deveMapearPerfilCompletoComAvaliacoesEAnuncios() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario vendedor = anuncio.getVendedor();
        Avaliacao avaliacao = AvaliacaoFactory.get();
        avaliacao.setVendedor(vendedor);

        // Act
        PerfilVendedorResponse response = mapper.toResponse(
                vendedor,
                4.5,
                1L,
                1L,
                2L,
                List.of(avaliacao),
                List.of(anuncio)
        );

        // Assert
        assertEquals(vendedor.getId(), response.id());
        assertEquals(vendedor.getNome(), response.nome());
        assertEquals(vendedor.getImagemPerfil(), response.imagemPerfil());
        assertEquals(vendedor.getCriadoEm(), response.membroDesde());
        assertEquals(4.5, response.notaMedia());
        assertEquals(1L, response.quantidadeAvaliacoes());
        assertEquals(1L, response.quantidadeProdutos());
        assertEquals(2L, response.quantidadeVendas());

        assertEquals(1, response.avaliacoes().size());
        assertEquals(avaliacao.getId(), response.avaliacoes().getFirst().id());
        assertEquals(avaliacao.getPedido().getId(), response.avaliacoes().getFirst().pedidoId());
        assertEquals(avaliacao.getAutor().getId(), response.avaliacoes().getFirst().autorId());
        assertEquals(avaliacao.getAutor().getNome(), response.avaliacoes().getFirst().autorNome());
        assertEquals(avaliacao.getAutor().getImagemPerfil(), response.avaliacoes().getFirst().autorImagemPerfil());
        assertEquals(avaliacao.getNota(), response.avaliacoes().getFirst().nota());
        assertEquals(avaliacao.getComentario(), response.avaliacoes().getFirst().comentario());
        assertEquals(avaliacao.getCriadoEm(), response.avaliacoes().getFirst().criadoEm());

        assertEquals(1, response.anuncios().size());
        assertEquals(anuncio.getId(), response.anuncios().getFirst().id());
        assertEquals(anuncio.getNome(), response.anuncios().getFirst().nome());
        assertEquals(anuncio.getPreco(), response.anuncios().getFirst().preco());
        assertEquals(anuncio.obterImagemPrincipal(), response.anuncios().getFirst().imagemPrincipal());
        assertEquals(anuncio.getCategoria().getId(), response.anuncios().getFirst().categoriaId());
        assertEquals(anuncio.getCategoria().getNome(), response.anuncios().getFirst().categoriaNome());
        assertEquals(anuncio.getCondicao(), response.anuncios().getFirst().condicao());
        assertEquals(anuncio.getConservacao(), response.anuncios().getFirst().conservacao());
        assertEquals(anuncio.getQuantidadeDisponivel(), response.anuncios().getFirst().quantidadeDisponivel());
        assertEquals(anuncio.getCriadoEm(), response.anuncios().getFirst().criadoEm());
    }

    @Test
    @DisplayName("Deve mapear perfil do vendedor sem histórico com notaMedia nula e listas vazias")
    void deveMapearPerfilSemHistorico() {
        // Arrange
        Usuario vendedor = AnuncioFactory.get().getVendedor();

        // Act
        PerfilVendedorResponse response = mapper.toResponse(
                vendedor,
                null,
                0L,
                0L,
                0L,
                List.of(),
                List.of()
        );

        // Assert
        assertEquals(vendedor.getId(), response.id());
        assertNull(response.notaMedia());
        assertEquals(0L, response.quantidadeAvaliacoes());
        assertEquals(0L, response.quantidadeProdutos());
        assertEquals(0L, response.quantidadeVendas());
        assertTrue(response.avaliacoes().isEmpty());
        assertTrue(response.anuncios().isEmpty());
    }
}
