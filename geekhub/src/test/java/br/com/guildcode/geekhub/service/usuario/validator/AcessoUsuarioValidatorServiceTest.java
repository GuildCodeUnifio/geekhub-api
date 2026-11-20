package br.com.guildcode.geekhub.service.usuario.validator;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcessoUsuarioValidatorServiceTest {

    private final AcessoUsuarioValidatorService validatorService = new AcessoUsuarioValidatorService();

    @Test
    @DisplayName("Deve permitir visualização quando usuário autenticado consultar os próprios dados")
    void devePermitirVisualizacaoParaProprioUsuario() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarVisualizacao(usuario.getId(), usuario));
    }

    @Test
    @DisplayName("Deve permitir visualização de outro usuário quando usuário autenticado for ADMIN")
    void devePermitirVisualizacaoParaAdmin() {

        // Arrange
        Usuario admin = UsuarioFactory.getAdmin();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarVisualizacao(1L, admin));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário comum tentar visualizar outro usuário")
    void deveBloquearVisualizacaoParaOutroUsuario() {

        // Arrange
        Usuario usuarioComum = UsuarioFactory.get();

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validatorService.validarVisualizacao(2L, usuarioComum)
        );

        assertEquals("Você não possui permissão para visualizar este usuário", ex.getMessage());
    }

    @Test
    @DisplayName("Deve permitir edição quando usuário autenticado alterar os próprios dados")
    void devePermitirEdicaoParaProprioUsuario() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarEdicao(usuario.getId(), usuario));
    }

    @Test
    @DisplayName("Deve permitir edição de outro usuário quando usuário autenticado for ADMIN")
    void devePermitirEdicaoParaAdmin() {

        // Arrange
        Usuario admin = UsuarioFactory.getAdmin();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarEdicao(1L, admin));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário comum tentar editar outro usuário")
    void deveBloquearEdicaoParaOutroUsuario() {

        // Arrange
        Usuario usuarioComum = UsuarioFactory.get();

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validatorService.validarEdicao(2L, usuarioComum)
        );

        assertEquals("Você não possui permissão para editar este usuário", ex.getMessage());
    }
}
