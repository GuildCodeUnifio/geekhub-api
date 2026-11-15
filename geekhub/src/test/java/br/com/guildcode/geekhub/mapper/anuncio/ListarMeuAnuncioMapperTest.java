package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarMeuAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ListarMeuAnuncioMapperTest {

    private final ListarMeuAnuncioMapper mapper = new ListarMeuAnuncioMapper();

    @Test
    @DisplayName("Deve mapear anúncio próprio usando a primeira imagem como imagem principal")
    void deveMapearComPrimeiraImagemComoPrincipal() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setImagens(List.of("https://picsum.photos/200", "https://picsum.photos/201"));

        // Act
        ListarMeuAnuncioResponse response = mapper.toResponse(anuncio);

        // Assert
        assertEquals(anuncio.getId(), response.id());
        assertEquals(anuncio.getNome(), response.nome());
        assertEquals(anuncio.getPreco(), response.preco());
        assertEquals(anuncio.getQuantidadeDisponivel(), response.quantidadeDisponivel());
        assertEquals(anuncio.getDisponivel(), response.disponivel());
        assertEquals(anuncio.getCondicao(), response.condicao());
        assertEquals(anuncio.getConservacao(), response.conservacao());
        assertEquals(anuncio.getCategoria().getNome(), response.categoriaNome());
        assertEquals("https://picsum.photos/200", response.imagemPrincipal());
        assertEquals(anuncio.getCriadoEm(), response.criadoEm());
    }

    @Test
    @DisplayName("Deve retornar imagem principal nula quando lista de imagens estiver vazia")
    void deveRetornarImagemPrincipalNulaQuandoListaVazia() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setImagens(List.of());

        // Act
        ListarMeuAnuncioResponse response = mapper.toResponse(anuncio);

        // Assert
        assertNull(response.imagemPrincipal());
    }

    @Test
    @DisplayName("Deve retornar imagem principal nula quando lista de imagens for nula")
    void deveRetornarImagemPrincipalNulaQuandoListaNula() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setImagens(null);

        // Act
        ListarMeuAnuncioResponse response = mapper.toResponse(anuncio);

        // Assert
        assertNull(response.imagemPrincipal());
    }
}
