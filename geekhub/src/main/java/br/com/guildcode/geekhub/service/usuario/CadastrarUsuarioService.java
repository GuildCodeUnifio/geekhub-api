package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.response.CadastrarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Permissao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.usuario.CadastrarUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import br.com.guildcode.geekhub.service.usuario.validator.EmailUnicoValidatorService;
import br.com.guildcode.geekhub.validator.usuario.CadastrarUsuarioValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

@Service
public class CadastrarUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CadastrarUsuarioValidator cadastrarUsuarioValidator;
    private final EmailUnicoValidatorService emailUnicoValidatorService;
    private final CadastrarUsuarioMapper cadastrarUsuarioMapper;

    public CadastrarUsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            CadastrarUsuarioValidator cadastrarUsuarioValidator,
            EmailUnicoValidatorService emailUnicoValidatorService,
            CadastrarUsuarioMapper cadastrarUsuarioMapper) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.cadastrarUsuarioValidator = cadastrarUsuarioValidator;
        this.emailUnicoValidatorService = emailUnicoValidatorService;
        this.cadastrarUsuarioMapper = cadastrarUsuarioMapper;
    }

    public CadastrarUsuarioResponse cadastrar(CadastrarUsuarioRequest request) {

        cadastrarUsuarioValidator.validarDadosCadastro(request);
        emailUnicoValidatorService.validar(request.email());

        Usuario usuario = cadastrarUsuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setAtivo(TRUE);
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.adicionarPermissao(Permissao.builder().nome("USUARIO").build());

        usuarioRepository.save(usuario);

        return cadastrarUsuarioMapper.toResponse(usuario);
    }
}
