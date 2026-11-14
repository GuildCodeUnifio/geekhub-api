package br.com.guildcode.geekhub.service.auth;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoService(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    public String getEmail() {

        Authentication authentication = getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getCredentials();

        return jwt.getClaim("email");
    }

    public Usuario get() {

        return usuarioRepository.findByEmail(getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não existe ou não está autenticado"));
    }
}
