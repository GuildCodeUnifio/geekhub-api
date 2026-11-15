package br.com.guildcode.geekhub.mapper.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.DetalharCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetalharCategoriaMapperTest {

    private final DetalharCategoriaMapper mapper = new DetalharCategoriaMapper();

    @Test
    @DisplayName("Deve mapear entidade Categoria para DetalharCategoriaResponse")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();

        // Act
        DetalharCategoriaResponse response = mapper.toResponse(categoria);

        // Assert
        assertEquals(categoria.getId(), response.id());
        assertEquals(categoria.getNome(), response.nome());
        assertEquals(categoria.getDescricao(), response.descricao());
        assertEquals(categoria.getAtiva(), response.ativa());
        assertEquals(categoria.getCriadoEm(), response.criadoEm());
    }
}
