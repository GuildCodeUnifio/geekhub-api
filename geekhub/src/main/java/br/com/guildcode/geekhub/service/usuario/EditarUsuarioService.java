package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.request.EditarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.dto.response.EditarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.usuario.EditarUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.usuario.validator.AcessoUsuarioValidatorService;
import br.com.guildcode.geekhub.service.usuario.validator.EmailUnicoValidatorService;
import br.com.guildcode.geekhub.validator.usuario.EditarUsuarioValidator;
import org.springframework.stereotype.Service;

@Service
public class EditarUsuarioService {

    private final EditarUsuarioValidator editarUsuarioValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final AcessoUsuarioValidatorService acessoUsuarioValidatorService;
    private final BuscarUsuarioService buscarUsuarioService;
    private final EmailUnicoValidatorService emailUnicoValidatorService;
    private final UsuarioRepository usuarioRepository;
    private final EditarUsuarioMapper editarUsuarioMapper;

    public EditarUsuarioService(
            EditarUsuarioValidator editarUsuarioValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            AcessoUsuarioValidatorService acessoUsuarioValidatorService,
            BuscarUsuarioService buscarUsuarioService,
            EmailUnicoValidatorService emailUnicoValidatorService,
            UsuarioRepository usuarioRepository,
            EditarUsuarioMapper editarUsuarioMapper) {

        this.editarUsuarioValidator = editarUsuarioValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.acessoUsuarioValidatorService = acessoUsuarioValidatorService;
        this.buscarUsuarioService = buscarUsuarioService;
        this.emailUnicoValidatorService = emailUnicoValidatorService;
        this.usuarioRepository = usuarioRepository;
        this.editarUsuarioMapper = editarUsuarioMapper;
    }

    public EditarUsuarioResponse editar(Long id, EditarUsuarioRequest request) {

        editarUsuarioValidator.validarDadosEdicao(request);

        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        acessoUsuarioValidatorService.validarEdicao(id, usuarioAutenticado);

        Usuario usuario = buscarUsuarioService.buscarPorId(id);

        if (!usuario.getEmail().equalsIgnoreCase(request.email())) {
            emailUnicoValidatorService.validar(request.email());
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setImagemPerfil(request.imagemPerfil());

        usuarioRepository.save(usuario);

        return editarUsuarioMapper.toResponse(usuario);
    }
}
