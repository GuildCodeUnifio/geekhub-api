package br.com.guildcode.geekhub.domain.anuncio;

import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    @DisplayName("Deve descontar estoque mantendo anúncio disponível quando restar quantidade positiva")
    void deveDescontarEstoqueMantendoDisponivelQuandoRestarEstoquePositivo() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setQuantidadeDisponivel(3);
        anuncio.setDisponivel(TRUE);

        // Act
        anuncio.descontarEstoque(2);

        // Assert
        assertEquals(1, anuncio.getQuantidadeDisponivel());
        assertTrue(anuncio.getDisponivel());
    }

    @Test
    @DisplayName("Deve descontar estoque e marcar anúncio como indisponível quando estoque zerar")
    void deveDescontarEstoqueEMarcarIndisponivelQuandoZerar() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setQuantidadeDisponivel(2);
        anuncio.setDisponivel(TRUE);

        // Act
        anuncio.descontarEstoque(2);

        // Assert
        assertEquals(0, anuncio.getQuantidadeDisponivel());
        assertFalse(anuncio.getDisponivel());
        assertEquals(FALSE, anuncio.getDisponivel());
    }

    @Test
    @DisplayName("Deve estornar estoque e reativar anúncio quando estava indisponível por falta de estoque")
    void deveEstornarEstoqueEReativarAnuncioQuandoEstavaEsgotado() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setQuantidadeDisponivel(0);
        anuncio.setDisponivel(FALSE);

        // Act
        anuncio.estornarEstoque(2);

        // Assert
        assertEquals(2, anuncio.getQuantidadeDisponivel());
        assertTrue(anuncio.getDisponivel());
    }

    @Test
    @DisplayName("Deve somar quantidade estornada mantendo anúncio disponível quando já havia estoque")
    void deveSomarQuantidadeEstornadaQuandoJaHaviaEstoque() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setQuantidadeDisponivel(3);
        anuncio.setDisponivel(TRUE);

        // Act
        anuncio.estornarEstoque(2);

        // Assert
        assertEquals(5, anuncio.getQuantidadeDisponivel());
        assertTrue(anuncio.getDisponivel());
    }

    @Test
    @DisplayName("Deve manter anúncio indisponível quando quantidade permanecer zerada após estorno")
    void deveManterIndisponivelQuandoEstoquePermaneceZerado() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setQuantidadeDisponivel(0);
        anuncio.setDisponivel(FALSE);

        // Act
        anuncio.estornarEstoque(0);

        // Assert
        assertEquals(0, anuncio.getQuantidadeDisponivel());
        assertFalse(anuncio.getDisponivel());
    }
}
