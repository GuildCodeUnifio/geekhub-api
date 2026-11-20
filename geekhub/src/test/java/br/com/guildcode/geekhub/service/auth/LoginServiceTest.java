package br.com.guildcode.geekhub.service.auth;

import br.com.guildcode.geekhub.controller.auth.dto.request.LoginRequest;
import br.com.guildcode.geekhub.controller.auth.dto.response.LoginResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.auth.LoginRequestFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.service.usuario.BuscarUsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks
    private LoginService service;

    @Mock
    private BuscarUsuarioService buscarUsuarioService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas e retornar token JWT")
    void deveAutenticarERetornarTokenJwt() {

        // Arrange
        LoginRequest request = LoginRequestFactory.get();
        Usuario usuario = UsuarioFactory.get();
        Jwt jwt = mock(Jwt.class);

        when(buscarUsuarioService.buscarPorEmail(request.email())).thenReturn(of(usuario));
        when(passwordEncoder.matches(request.senha(), usuario.getSenha())).thenReturn(true);
        when(jwt.getTokenValue()).thenReturn("jwt-token-gerado");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResponse response = service.login(request);

        // Assert
        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals("jwt-token-gerado", response.accessToken());
        assertEquals(3600L, response.expiresIn());
        assertEquals(usuario.getId(), claims.getClaim("id"));
        assertEquals(usuario.getEmail(), claims.getClaim("email"));
        assertEquals(List.of("USUARIO"), claims.getClaim("scope"));
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando e-mail não for encontrado")
    void deveLancarExcecaoQuandoEmailNaoEncontrado() {

        // Arrange
        LoginRequest request = LoginRequestFactory.get();
        when(buscarUsuarioService.buscarPorEmail(request.email())).thenReturn(empty());

        // Act & Assert
        BadCredentialsException ex = assertThrows(
                BadCredentialsException.class,
                () -> service.login(request)
        );

        assertEquals("Usuário ou senha incorretos", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar BadCredentialsException quando a senha estiver incorreta")
    void deveLancarExcecaoQuandoSenhaIncorreta() {

        // Arrange
        LoginRequest request = LoginRequestFactory.get();
        Usuario usuario = UsuarioFactory.get();

        when(buscarUsuarioService.buscarPorEmail(request.email())).thenReturn(of(usuario));
        when(passwordEncoder.matches(request.senha(), usuario.getSenha())).thenReturn(false);

        // Act & Assert
        BadCredentialsException ex = assertThrows(
                BadCredentialsException.class,
                () -> service.login(request)
        );

        assertEquals("Usuário ou senha incorretos", ex.getMessage());
    }
}
