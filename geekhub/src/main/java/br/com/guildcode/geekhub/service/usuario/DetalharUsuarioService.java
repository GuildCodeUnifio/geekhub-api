package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.usuario.DetalharUsuarioMapper;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.usuario.validator.AcessoUsuarioValidatorService;
import org.springframework.stereotype.Service;

@Service
public class DetalharUsuarioService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final AcessoUsuarioValidatorService acessoUsuarioValidatorService;
    private final BuscarUsuarioService buscarUsuarioService;
    private final DetalharUsuarioMapper detalharUsuarioMapper;

    public DetalharUsuarioService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            AcessoUsuarioValidatorService acessoUsuarioValidatorService,
            BuscarUsuarioService buscarUsuarioService,
            DetalharUsuarioMapper detalharUsuarioMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.acessoUsuarioValidatorService = acessoUsuarioValidatorService;
        this.buscarUsuarioService = buscarUsuarioService;
        this.detalharUsuarioMapper = detalharUsuarioMapper;
    }

    public DetalharUsuarioResponse detalhar(Long id) {

        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        acessoUsuarioValidatorService.validarVisualizacao(id, usuarioAutenticado);

        Usuario usuario = buscarUsuarioService.buscarPorId(id);

        return detalharUsuarioMapper.toResponse(usuario);
    }
}
