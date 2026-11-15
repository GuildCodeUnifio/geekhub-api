package br.com.guildcode.geekhub.domain.anuncio;

import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AnuncioTest {

    @Test
    @DisplayName("Deve retornar a primeira imagem como imagem principal quando houver imagens")
    void deveRetornarPrimeiraImagemQuandoHouverImagens() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setImagens(List.of("https://picsum.photos/200", "https://picsum.photos/201"));

        // Act
        String imagemPrincipal = anuncio.obterImagemPrincipal();

        // Assert
        assertEquals("https://picsum.photos/200", imagemPrincipal);
    }

    @Test
    @DisplayName("Deve retornar nulo quando lista de imagens estiver vazia")
    void deveRetornarNuloQuandoListaImagensEstiverVazia() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setImagens(Collections.emptyList());

        // Act
        String imagemPrincipal = anuncio.obterImagemPrincipal();

        // Assert
        assertNull(imagemPrincipal);
    }

    @Test
    @DisplayName("Deve retornar nulo quando lista de imagens for nula")
    void deveRetornarNuloQuandoListaImagensForNula() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setImagens(null);

        // Act
        String imagemPrincipal = anuncio.obterImagemPrincipal();

        // Assert
        assertNull(imagemPrincipal);
    }
}
