package br.com.guildcode.geekhub.validator.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.usuario.CadastrarUsuarioRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CadastrarUsuarioValidatorTest {

    private final CadastrarUsuarioValidator validator = new CadastrarUsuarioValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando os dados de cadastro forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {

        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(null)
        );

        assertEquals("Dados do usuário são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando os dados de cadastro forem válidos")
    void naoDeveLancarExcecaoQuandoRequestValido() {

        // Arrange
        CadastrarUsuarioRequest request = CadastrarUsuarioRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosCadastro(request));
    }
}
