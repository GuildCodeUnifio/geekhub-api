package br.com.guildcode.geekhub.service.auth;

import br.com.guildcode.geekhub.controller.auth.request.LoginRequest;
import br.com.guildcode.geekhub.controller.auth.response.LoginResponse;
import br.com.guildcode.geekhub.domain.usuario.Permissao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.service.usuario.BuscarUsuarioService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.oauth2.jwt.JwtClaimsSet.builder;
import static org.springframework.security.oauth2.jwt.JwtEncoderParameters.from;

@Service
public class LoginService {

    private final BuscarUsuarioService buscarUsuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public LoginService(
            BuscarUsuarioService buscarUsuarioService,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder) {

        this.buscarUsuarioService = buscarUsuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {

        Optional<Usuario> optUser = buscarUsuarioService.buscarPorEmail(loginRequest.email());

        if (optUser.isEmpty() || !senhaCorreta(loginRequest.senha(), optUser.get().getSenha())) {

            throw new BadCredentialsException("Usuário ou senha incorretos");
        }

        Usuario usuario = optUser.get();
        List<String> permissoes = usuario.getPermissoes().stream()
                .map(Permissao::getNome)
                .toList();
        Long expiresIn = 3600L;

        JwtClaimsSet claims = builder()
                .issuer("geekhub-api")
                .subject(usuario.getNome())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .claim("email", usuario.getEmail())
                .claim("scope", permissoes)
                .build();

        String token = jwtEncoder.encode(from(claims)).getTokenValue();

        return LoginResponse.builder()
                .accessToken(token)
                .expiresIn(expiresIn)
                .build();
    }

    private Boolean senhaCorreta(String senhaInformada, String senhaSalva) {

        return passwordEncoder.matches(senhaInformada, senhaSalva);
    }
}
