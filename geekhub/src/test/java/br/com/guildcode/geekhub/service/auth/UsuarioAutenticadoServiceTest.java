package br.com.guildcode.geekhub.service.auth;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@ExtendWith(MockitoExtension.class)
class UsuarioAutenticadoServiceTest {

    @InjectMocks
    private UsuarioAutenticadoService service;

    @Mock
    private UsuarioRepository usuarioRepository;

    @AfterEach
    void limparContexto() {

        SecurityContextHolder.clearContext();
    }

    private void configurarContextoDeSeguranca(String email) {

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("email")).thenReturn(email);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn(jwt);

        getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("Deve retornar o e-mail extraído do JWT no contexto de segurança")
    void deveRetornarEmailDoUsuarioAutenticado() {

        // Arrange
        configurarContextoDeSeguranca("rafael@email.com");

        // Act
        String email = service.getEmail();

        // Assert
        assertEquals("rafael@email.com", email);
    }

    @Test
    @DisplayName("Deve retornar o usuário correspondente ao e-mail do JWT")
    void deveRetornarUsuarioAutenticado() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        configurarContextoDeSeguranca(usuario.getEmail());
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(of(usuario));

        // Act
        Usuario resultado = service.get();

        // Assert
        assertEquals(usuario.getId(), resultado.getId());
        assertEquals(usuario.getEmail(), resultado.getEmail());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando usuário do JWT não existe no banco")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {

        // Arrange
        configurarContextoDeSeguranca("naoexiste@email.com");
        when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(empty());

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.get()
        );

        assertEquals("Usuário não existe ou não está autenticado", ex.getMessage());
    }
}
