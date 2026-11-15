package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.DetalharAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DetalharAnuncioMapperTest {

    private final DetalharAnuncioMapper mapper = new DetalharAnuncioMapper();

    @Test
    @DisplayName("Deve mapear anúncio com resumo público do vendedor e estatísticas de avaliações")
    void deveMapearAnuncioComResumoPublicoDoVendedor() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.getVendedor().setImagemPerfil("https://picsum.photos/200");

        // Act
        DetalharAnuncioResponse response = mapper.toResponse(anuncio, 4.75, 4L);

        // Assert
        assertEquals(anuncio.getId(), response.id());
        assertEquals(anuncio.getNome(), response.nome());
        assertEquals(anuncio.getDescricao(), response.descricao());
        assertEquals(anuncio.getCategoria().getId(), response.categoriaId());
        assertEquals(anuncio.getCategoria().getNome(), response.categoriaNome());
        assertEquals(anuncio.getPreco(), response.preco());
        assertEquals(anuncio.getQuantidadeDisponivel(), response.quantidadeDisponivel());
        assertEquals(anuncio.getDisponivel(), response.disponivel());
        assertEquals(anuncio.getCondicao(), response.condicao());
        assertEquals(anuncio.getConservacao(), response.conservacao());
        assertEquals(anuncio.getImagens(), response.imagens());
        assertEquals(anuncio.getCriadoEm(), response.criadoEm());

        assertNotNull(response.vendedor());
        assertEquals(anuncio.getVendedor().getId(), response.vendedor().id());
        assertEquals(anuncio.getVendedor().getNome(), response.vendedor().nome());
        assertEquals(anuncio.getVendedor().getImagemPerfil(), response.vendedor().imagemPerfil());
        assertEquals(4.75, response.vendedor().notaMedia());
        assertEquals(4L, response.vendedor().quantidadeAvaliacoes());
    }

    @Test
    @DisplayName("Deve mapear vendedor sem avaliações com nota média nula")
    void deveMapearVendedorSemAvaliacoesComNotaMediaNula() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();

        // Act
        DetalharAnuncioResponse response = mapper.toResponse(anuncio, null, 0L);

        // Assert
        assertNotNull(response.vendedor());
        assertNull(response.vendedor().notaMedia());
        assertEquals(0L, response.vendedor().quantidadeAvaliacoes());
    }
}
