package br.com.guildcode.geekhub.mapper.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.ListarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarCategoriaMapperTest {

    private final ListarCategoriaMapper mapper = new ListarCategoriaMapper();

    @Test
    @DisplayName("Deve mapear entidade Categoria para ListarCategoriaResponse")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();

        // Act
        ListarCategoriaResponse response = mapper.toResponse(categoria);

        // Assert
        assertEquals(categoria.getId(), response.id());
        assertEquals(categoria.getNome(), response.nome());
        assertEquals(categoria.getDescricao(), response.descricao());
        assertEquals(categoria.getAtiva(), response.ativa());
        assertEquals(categoria.getCriadoEm(), response.criadoEm());
    }
}
