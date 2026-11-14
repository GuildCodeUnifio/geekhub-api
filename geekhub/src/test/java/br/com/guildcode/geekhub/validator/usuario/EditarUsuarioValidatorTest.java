package br.com.guildcode.geekhub.validator.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.EditarUsuarioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.usuario.EditarUsuarioRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EditarUsuarioValidatorTest {

    private final EditarUsuarioValidator validator = new EditarUsuarioValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando os dados de edição forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {

        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosEdicao(null)
        );

        assertEquals("Dados do usuário são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando os dados de edição forem válidos")
    void naoDeveLancarExcecaoQuandoRequestValido() {

        // Arrange
        EditarUsuarioRequest request = EditarUsuarioRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosEdicao(request));
    }
}
