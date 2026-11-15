package br.com.guildcode.geekhub.mapper.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.CadastrarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.factory.categoria.CadastrarCategoriaRequestFactory;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CadastrarCategoriaMapperTest {

    private final CadastrarCategoriaMapper mapper = new CadastrarCategoriaMapper();

    @Test
    @DisplayName("Deve mapear CadastrarCategoriaRequest para entidade Categoria")
    void deveMapearRequestParaEntidade() {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequestFactory.get();

        // Act
        Categoria entity = mapper.toEntity(request);

        // Assert
        assertEquals(request.nome(), entity.getNome());
        assertEquals(request.descricao(), entity.getDescricao());
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Deve mapear entidade Categoria para CadastrarCategoriaResponse")
    void deveMapearEntidadeParaResponse() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();

        // Act
        CadastrarCategoriaResponse response = mapper.toResponse(categoria);

        // Assert
        assertEquals(categoria.getId(), response.id());
        assertEquals(categoria.getNome(), response.nome());
        assertEquals(categoria.getDescricao(), response.descricao());
        assertEquals(categoria.getAtiva(), response.ativa());
        assertEquals(categoria.getCriadoEm(), response.criadoEm());
    }
}
