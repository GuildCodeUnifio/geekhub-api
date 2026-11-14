package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoverUsuarioService {

    private final BuscarUsuarioService buscarUsuarioService;
    private final UsuarioRepository usuarioRepository;

    public RemoverUsuarioService(
            BuscarUsuarioService buscarUsuarioService,
            UsuarioRepository usuarioRepository) {

        this.buscarUsuarioService = buscarUsuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    public void remover(Long id) {

        Usuario usuario = buscarUsuarioService.buscarPorId(id);

        usuarioRepository.delete(usuario);
    }
}
