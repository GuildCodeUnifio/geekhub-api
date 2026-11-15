package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditarAnuncioMapperTest {

    private final EditarAnuncioMapper mapper = new EditarAnuncioMapper();

    @Test
    @DisplayName("Deve mapear entidade Anuncio para EditarAnuncioResponse")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();

        // Act
        EditarAnuncioResponse response = mapper.toResponse(anuncio);

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
