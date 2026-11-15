package br.com.guildcode.geekhub.validator.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.categoria.CadastrarCategoriaRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CadastrarCategoriaValidatorTest {

    private final CadastrarCategoriaValidator validator = new CadastrarCategoriaValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando os dados de cadastro da categoria forem nulos")
    void deveLancarExcecaoQuandoRequestNulo() {

        // Arrange, Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosCadastro(null)
        );

        assertEquals("Dados da categoria são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando os dados de cadastro da categoria forem válidos")
    void naoDeveLancarExcecaoQuandoRequestValido() {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosCadastro(request));
    }
}
