package br.com.guildcode.geekhub.validator.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.categoria.EditarCategoriaRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EditarCategoriaValidatorTest {

    private final EditarCategoriaValidator validator = new EditarCategoriaValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando os dados de edição da categoria forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {

        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(null)
        );

        assertEquals("Dados da categoria são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando os dados de edição da categoria forem válidos")
    void naoDeveLancarExcecaoQuandoRequestValido() {

        // Arrange
        EditarCategoriaRequest request = EditarCategoriaRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosEdicao(request));
    }
}
