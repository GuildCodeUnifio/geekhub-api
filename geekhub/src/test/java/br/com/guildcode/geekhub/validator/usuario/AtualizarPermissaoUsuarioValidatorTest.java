package br.com.guildcode.geekhub.validator.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.request.AtualizarPermissaoUsuarioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.usuario.AtualizarPermissaoUsuarioRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AtualizarPermissaoUsuarioValidatorTest {

    private final AtualizarPermissaoUsuarioValidator validator = new AtualizarPermissaoUsuarioValidator();

    @Test
    @DisplayName("Deve validar dados obrigatórios quando requisição e indicador admin forem informados")
    void deveValidarDadosComSucesso() {

        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosAtualizacao(request));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando requisição de permissão for nula")
    void deveLancarExcecaoQuandoRequestNulo() {

        // Act & Assert
        NegocioException ex = assertThrows(NegocioException.class,
                () -> validator.validarDadosAtualizacao(null));

        assertEquals("Dados de atualização de permissão são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando indicador admin for nulo")
    void deveLancarExcecaoQuandoAdminNulo() {

        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequest.builder()
                .admin(null)
                .build();

        // Act & Assert
        NegocioException ex = assertThrows(NegocioException.class,
                () -> validator.validarDadosAtualizacao(request));

        assertEquals("O indicador de administrador é obrigatório", ex.getMessage());
    }
}
