package br.com.guildcode.geekhub.service.avaliacao.validator;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutorAvaliacaoValidatorServiceTest {

    private final AutorAvaliacaoValidatorService service = new AutorAvaliacaoValidatorService();

    @Test
    @DisplayName("Não deve lançar exceção quando usuário for o autor ao validar edição da avaliação")
    void devePermitirEdicaoQuandoUsuarioForAutor() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario autor = avaliacao.getAutor();

        // Act & Assert
        assertDoesNotThrow(() -> service.validarAutor(avaliacao, autor));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário não for o autor ao validar edição da avaliação")
    void deveLancarAccessDeniedQuandoUsuarioNaoForAutorNaEdicao() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario adminNaoAutor = UsuarioFactory.getAdmin();

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.validarAutor(avaliacao, adminNaoAutor)
        );

        assertEquals("Você não possui permissão para editar esta avaliação", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando usuário for o autor ao validar remoção da avaliação")
    void devePermitirRemocaoQuandoUsuarioForAutor() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario autor = avaliacao.getAutor();

        // Act & Assert
        assertDoesNotThrow(() -> service.validarAutorOuAdmin(avaliacao, autor));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando usuário não for o autor mas possuir perfil ADMIN ao validar remoção")
    void devePermitirRemocaoQuandoUsuarioForAdmin() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario adminNaoAutor = UsuarioFactory.getAdmin();

        // Act & Assert
        assertDoesNotThrow(() -> service.validarAutorOuAdmin(avaliacao, adminNaoAutor));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário comum não autor tentar validar remoção")
    void deveLancarAccessDeniedQuandoUsuarioComumNaoAutorNaRemocao() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        Usuario outroUsuario = UsuarioFactory.get();
        outroUsuario.setId(77L);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.validarAutorOuAdmin(avaliacao, outroUsuario)
        );

        assertEquals("Você não possui permissão para remover esta avaliação", ex.getMessage());
    }
}
