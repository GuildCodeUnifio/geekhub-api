package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.response.CadastrarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.CadastrarUsuarioRequestFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.usuario.CadastrarUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import br.com.guildcode.geekhub.service.usuario.validator.EmailUnicoValidatorService;
import br.com.guildcode.geekhub.validator.usuario.CadastrarUsuarioValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarUsuarioServiceTest {

    @InjectMocks
    private CadastrarUsuarioService service;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CadastrarUsuarioValidator cadastrarUsuarioValidator;

    @Mock
    private EmailUnicoValidatorService emailUnicoValidatorService;

    @Mock
    private CadastrarUsuarioMapper cadastrarUsuarioMapper;

    @Test
    @DisplayName("Deve cadastrar usuário, criptografar senha, definir ativo e salvar no repositório")
    void deveCadastrarUsuarioComSucesso() {

        // Arrange
        CadastrarUsuarioRequest request = CadastrarUsuarioRequestFactory.get();
        Usuario usuario = UsuarioFactory.get();
        CadastrarUsuarioResponse expectedResponse = CadastrarUsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .imagemPerfil(usuario.getImagemPerfil())
                .criadoEm(usuario.getCriadoEm())
                .build();

        doNothing().when(cadastrarUsuarioValidator).validarDadosCadastro(request);
        doNothing().when(emailUnicoValidatorService).validar(request.email());
        when(cadastrarUsuarioMapper.toEntity(request)).thenReturn(usuario);
        when(passwordEncoder.encode(request.senha())).thenReturn("senha_criptografada");
        when(cadastrarUsuarioMapper.toResponse(usuario)).thenReturn(expectedResponse);

        // Act
        CadastrarUsuarioResponse response = service.cadastrar(request);

        // Assert
        assertEquals(expectedResponse.nome(), response.nome());
        assertEquals(expectedResponse.email(), response.email());
        assertEquals("senha_criptografada", usuario.getSenha());
        assertEquals(TRUE, usuario.getAtivo());
        assertNotNull(usuario.getCriadoEm());

        verify(cadastrarUsuarioValidator).validarDadosCadastro(request);
        verify(emailUnicoValidatorService).validar(request.email());
        verify(cadastrarUsuarioMapper).toEntity(request);
        verify(passwordEncoder).encode(request.senha());
        verify(usuarioRepository).save(usuario);
        verify(cadastrarUsuarioMapper).toResponse(usuario);
    }
}
