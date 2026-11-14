package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BuscarUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public BuscarUsuarioService(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> buscarPorEmail(String email) {

        return usuarioRepository.findByEmail(email);
    }

    public Usuario buscarPorId(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
