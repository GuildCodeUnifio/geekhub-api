package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.anuncio.CadastrarAnuncioRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CadastrarAnuncioMapperTest {

    private final CadastrarAnuncioMapper mapper = new CadastrarAnuncioMapper();

    @Test
    @DisplayName("Deve mapear CadastrarAnuncioRequest para entidade Anuncio")
    void deveMapearRequestParaEntidade() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();

        // Act
        Anuncio entity = mapper.toEntity(request);

        // Assert
        assertNull(entity.getId());
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.descricao(), entity.getDescricao());
        assertEquals(request.preco(), entity.getPreco());
        assertEquals(request.quantidadeDisponivel(), entity.getQuantidadeDisponivel());
        assertEquals(request.condicao(), entity.getCondicao());
        assertEquals(request.conservacao(), entity.getConservacao());
        assertEquals(request.imagens(), entity.getImagens());
    }

    @Test
    @DisplayName("Deve mapear entidade Anuncio para CadastrarAnuncioResponse")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();

        // Act
        CadastrarAnuncioResponse response = mapper.toResponse(anuncio);

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
        assertEquals(anuncio.getVendedor().getId(), response.vendedorId());
        assertEquals(anuncio.getCriadoEm(), response.criadoEm());
    }
}
