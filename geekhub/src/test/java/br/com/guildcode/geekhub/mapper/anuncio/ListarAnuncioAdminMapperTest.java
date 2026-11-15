package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioAdminResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarAnuncioAdminMapperTest {

    private final ListarAnuncioAdminMapper mapper = new ListarAnuncioAdminMapper();

    @Test
    @DisplayName("Deve mapear anúncio com dados completos para listagem administrativa")
    void deveMapearAnuncioParaListagemAdmin() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();

        // Act
        ListarAnuncioAdminResponse response = mapper.toResponse(anuncio);

        // Assert
        assertEquals(anuncio.getId(), response.id());
        assertEquals(anuncio.getNome(), response.nome());
        assertEquals(anuncio.getPreco(), response.preco());
        assertEquals(anuncio.getQuantidadeDisponivel(), response.quantidadeDisponivel());
        assertEquals(anuncio.getDisponivel(), response.disponivel());
        assertEquals(anuncio.getCategoria().getNome(), response.categoriaNome());
        assertEquals(anuncio.getVendedor().getId(), response.vendedorId());
        assertEquals(anuncio.getVendedor().getNome(), response.vendedorNome());
        assertEquals(anuncio.getCriadoEm(), response.criadoEm());
    }
}
