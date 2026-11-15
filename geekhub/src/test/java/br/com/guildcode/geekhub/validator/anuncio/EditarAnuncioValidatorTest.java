package br.com.guildcode.geekhub.validator.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.anuncio.EditarAnuncioRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.USADO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.BOM;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EditarAnuncioValidatorTest {

    private final EditarAnuncioValidator validator = new EditarAnuncioValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando dados de edição forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {

        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(null)
        );

        assertEquals("Dados do anúncio são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando preço na edição for negativo")
    void deveLancarExcecaoQuandoPrecoNegativo() {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("-10.00"))
                .quantidadeDisponivel(2)
                .disponivel(TRUE)
                .condicao(USADO)
                .conservacao(BOM)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request)
        );

        assertEquals("Preço deve ser maior que zero", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade disponível na edição for nula")
    void deveLancarExcecaoQuandoQuantidadeNula() {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("100.00"))
                .quantidadeDisponivel(null)
                .disponivel(TRUE)
                .condicao(USADO)
                .conservacao(BOM)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request)
        );

        assertEquals("Quantidade disponível não pode ser negativa", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade disponível na edição for negativa")
    void deveLancarExcecaoQuandoQuantidadeNegativa() {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("100.00"))
                .quantidadeDisponivel(-1)
                .disponivel(TRUE)
                .condicao(USADO)
                .conservacao(BOM)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request)
        );

        assertEquals("Quantidade disponível não pode ser negativa", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando flag disponivel na edição for nula")
    void deveLancarExcecaoQuandoDisponivelNulo() {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("100.00"))
                .quantidadeDisponivel(2)
                .disponivel(null)
                .condicao(USADO)
                .conservacao(BOM)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(request)
        );

        assertEquals("Disponibilidade do anúncio é obrigatória", ex.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar edição com quantidade disponível zerada")
    void deveAceitarEdicaoComQuantidadeZero() {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.semEstoque();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosEdicao(request));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando dados de edição forem válidos")
    void naoDeveLancarExcecaoQuandoDadosValidos() {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosEdicao(request));
    }
}
