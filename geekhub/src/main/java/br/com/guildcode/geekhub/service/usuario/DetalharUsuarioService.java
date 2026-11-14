package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.usuario.DetalharUsuarioMapper;
import org.springframework.stereotype.Service;

@Service
public class DetalharUsuarioService {

    private final BuscarUsuarioService buscarUsuarioService;
    private final DetalharUsuarioMapper detalharUsuarioMapper;

    public DetalharUsuarioService(
            BuscarUsuarioService buscarUsuarioService,
            DetalharUsuarioMapper detalharUsuarioMapper) {

        this.buscarUsuarioService = buscarUsuarioService;
        this.detalharUsuarioMapper = detalharUsuarioMapper;
    }

    public DetalharUsuarioResponse detalhar(Long id) {

        Usuario usuario = buscarUsuarioService.buscarPorId(id);

        return detalharUsuarioMapper.toResponse(usuario);
    }
}
