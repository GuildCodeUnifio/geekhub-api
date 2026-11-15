package br.com.guildcode.geekhub.service.anuncio.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProprietarioAnuncioValidatorServiceTest {

    private final ProprietarioAnuncioValidatorService validatorService = new ProprietarioAnuncioValidatorService();

    @Test
    @DisplayName("Não deve lançar exceção quando usuário autenticado for o proprietário do anúncio")
    void devePermitirProprietarioEditar() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario proprietario = anuncio.getVendedor();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarProprietario(anuncio, proprietario));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando outro usuário tentar alterar anúncio")
    void deveBloquearOutroUsuarioNaEdicao() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario outroUsuario = UsuarioFactory.get();

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validatorService.validarProprietario(anuncio, outroUsuario)
        );

        assertEquals("Você não possui permissão para alterar este anúncio", ex.getMessage());
    }

    @Test
    @DisplayName("Deve permitir remoção quando usuário autenticado for o proprietário")
    void devePermitirProprietarioRemover() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario proprietario = anuncio.getVendedor();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarProprietarioOuAdmin(anuncio, proprietario));
    }

    @Test
    @DisplayName("Deve permitir remoção quando usuário autenticado for ADMIN")
    void devePermitirAdminRemover() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario admin = UsuarioFactory.getAdmin();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarProprietarioOuAdmin(anuncio, admin));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando terceiro sem perfil ADMIN tentar remover anúncio")
    void deveBloquearTerceiroNaRemocao() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario terceiro = UsuarioFactory.get();

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validatorService.validarProprietarioOuAdmin(anuncio, terceiro)
        );

        assertEquals("Você não possui permissão para remover este anúncio", ex.getMessage());
    }
}
