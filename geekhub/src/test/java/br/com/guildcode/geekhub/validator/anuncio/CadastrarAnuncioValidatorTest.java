package br.com.guildcode.geekhub.validator.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.anuncio.CadastrarAnuncioRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CadastrarAnuncioValidatorTest {

    private final CadastrarAnuncioValidator validator = new CadastrarAnuncioValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando dados do anúncio forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {

        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(null)
        );

        assertEquals("Dados do anúncio são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando preço for nulo")
    void deveLancarExcecaoQuandoPrecoNulo() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(null)
                .quantidadeDisponivel(1)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("Preço deve ser maior que zero", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando preço for menor ou igual a zero")
    void deveLancarExcecaoQuandoPrecoNaoPositivo() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(BigDecimal.ZERO)
                .quantidadeDisponivel(1)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("Preço deve ser maior que zero", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando lista de imagens for nula")
    void deveLancarExcecaoQuandoImagensNulas() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("100.00"))
                .quantidadeDisponivel(1)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(null)
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("Anúncio deve possuir ao menos uma imagem", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando lista de imagens estiver vazia")
    void deveLancarExcecaoQuandoImagensVazias() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("100.00"))
                .quantidadeDisponivel(1)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of())
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("Anúncio deve possuir ao menos uma imagem", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade disponível for nula")
    void deveLancarExcecaoQuandoQuantidadeNula() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("100.00"))
                .quantidadeDisponivel(null)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("Quantidade disponível deve ser maior que zero", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade disponível no cadastro for menor ou igual a zero")
    void deveLancarExcecaoQuandoQuantidadeZeroNoCadastro() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequest.builder()
                .nome("Produto")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(new BigDecimal("100.00"))
                .quantidadeDisponivel(0)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of("https://picsum.photos/200"))
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(request)
        );

        assertEquals("Quantidade disponível deve ser maior que zero", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando dados de cadastro do anúncio forem válidos")
    void naoDeveLancarExcecaoQuandoDadosValidos() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosCadastro(request));
    }
}
