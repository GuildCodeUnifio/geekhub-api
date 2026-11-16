package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.request.AtualizarPermissaoUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.dto.response.AtualizarPermissaoUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.usuario.AtualizarPermissaoUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.usuario.validator.AlteracaoPropriaPermissaoValidatorService;
import br.com.guildcode.geekhub.validator.usuario.AtualizarPermissaoUsuarioValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarPermissaoUsuarioService {

    private final AtualizarPermissaoUsuarioValidator atualizarPermissaoUsuarioValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarUsuarioService buscarUsuarioService;
    private final AlteracaoPropriaPermissaoValidatorService alteracaoPropriaPermissaoValidatorService;
    private final UsuarioRepository usuarioRepository;
    private final AtualizarPermissaoUsuarioMapper atualizarPermissaoUsuarioMapper;

    public AtualizarPermissaoUsuarioService(
            AtualizarPermissaoUsuarioValidator atualizarPermissaoUsuarioValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarUsuarioService buscarUsuarioService,
            AlteracaoPropriaPermissaoValidatorService alteracaoPropriaPermissaoValidatorService,
            UsuarioRepository usuarioRepository,
            AtualizarPermissaoUsuarioMapper atualizarPermissaoUsuarioMapper) {

        this.atualizarPermissaoUsuarioValidator = atualizarPermissaoUsuarioValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarUsuarioService = buscarUsuarioService;
        this.alteracaoPropriaPermissaoValidatorService = alteracaoPropriaPermissaoValidatorService;
        this.usuarioRepository = usuarioRepository;
        this.atualizarPermissaoUsuarioMapper = atualizarPermissaoUsuarioMapper;
    }

    @Transactional
    public AtualizarPermissaoUsuarioResponse atualizar(Long id, AtualizarPermissaoUsuarioRequest request) {

        atualizarPermissaoUsuarioValidator.validarDadosAtualizacao(request);

        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        Usuario usuario = buscarUsuarioService.buscarPorId(id);

        alteracaoPropriaPermissaoValidatorService.validar(usuarioAutenticado, usuario);

        usuario.atualizarPermissaoAdmin(request.admin());
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return atualizarPermissaoUsuarioMapper.toResponse(usuarioSalvo);
    }
}
