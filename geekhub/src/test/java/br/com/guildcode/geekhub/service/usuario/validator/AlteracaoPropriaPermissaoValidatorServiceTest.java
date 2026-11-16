package br.com.guildcode.geekhub.service.usuario.validator;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlteracaoPropriaPermissaoValidatorServiceTest {

    private final AlteracaoPropriaPermissaoValidatorService validator = new AlteracaoPropriaPermissaoValidatorService();

    @Test
    @DisplayName("Deve permitir alteração quando usuário alvo for diferente do administrador autenticado")
    void devePermitirAlteracaoQuandoUsuariosDiferentes() {

        // Arrange
        Usuario admin = UsuarioFactory.getAdmin();
        Usuario usuarioAlvo = UsuarioFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validar(admin, usuarioAlvo));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando administrador tentar alterar a própria permissão")
    void deveLancarExcecaoQuandoAlterarPropriaPermissao() {

        // Arrange
        Usuario admin = UsuarioFactory.getAdmin();

        // Act & Assert
        NegocioException ex = assertThrows(NegocioException.class,
                () -> validator.validar(admin, admin));

        assertEquals("Não é possível alterar a própria permissão de administrador", ex.getMessage());
    }
}
