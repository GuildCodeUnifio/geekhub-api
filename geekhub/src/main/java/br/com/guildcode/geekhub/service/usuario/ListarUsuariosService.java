package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.response.ListarUsuarioResponse;
import br.com.guildcode.geekhub.mapper.usuario.ListarUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListarUsuariosService {

    private final UsuarioRepository usuarioRepository;
    private final ListarUsuarioMapper listarUsuarioMapper;

    public ListarUsuariosService(
            UsuarioRepository usuarioRepository,
            ListarUsuarioMapper listarUsuarioMapper) {

        this.usuarioRepository = usuarioRepository;
        this.listarUsuarioMapper = listarUsuarioMapper;
    }

    public Page<ListarUsuarioResponse> listar(Pageable pageable) {

        return usuarioRepository.findAll(pageable)
                .map(listarUsuarioMapper::toResponse);
    }
}
