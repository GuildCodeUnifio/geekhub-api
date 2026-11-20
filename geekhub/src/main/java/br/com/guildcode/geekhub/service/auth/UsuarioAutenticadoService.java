package br.com.guildcode.geekhub.service.auth;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Service
public class UsuarioAutenticadoService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAutenticadoService(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    public Long getId() {

        Number id = getJwt().getClaim("id");
        return id != null ? Long.valueOf(id.longValue()) : null;
    }

    public String getEmail() {

        return getJwt().getClaim("email");
    }

    public Usuario get() {

        Long id = getId();
        Optional<Usuario> usuario = id != null
                ? usuarioRepository.findById(id)
                : usuarioRepository.findByEmail(getEmail());

        return usuario.orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não existe ou não está autenticado"));
    }

    private Jwt getJwt() {

        Authentication authentication = getContext().getAuthentication();
        return (Jwt) authentication.getCredentials();
    }
}
