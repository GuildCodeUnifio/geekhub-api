package br.com.guildcode.geekhub.service.usuario.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailUnicoValidatorServiceTest {

    @InjectMocks
    private EmailUnicoValidatorService validatorService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve lançar ConflitoException quando e-mail já está cadastrado no sistema")
    void deveLancarExcecaoQuandoEmailJaCadastrado() {

        // Arrange
        String email = UsuarioFactory.get().getEmail();
        when(usuarioRepository.existsByEmail(email)).thenReturn(TRUE);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> validatorService.validar(email)
        );

        assertEquals("E-mail já cadastrado", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando e-mail ainda não está cadastrado")
    void naoDeveLancarExcecaoQuandoEmailDisponivel() {

        // Arrange
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(FALSE);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar("novo@email.com"));
    }
}
