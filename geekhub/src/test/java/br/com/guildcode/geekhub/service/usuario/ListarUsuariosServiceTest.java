package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.response.ListarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.usuario.ListarUsuarioMapper;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Page.empty;
import static org.springframework.data.domain.PageRequest.of;

@ExtendWith(MockitoExtension.class)
class ListarUsuariosServiceTest {

    @InjectMocks
    private ListarUsuariosService service;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ListarUsuarioMapper listarUsuarioMapper;

    @Test
    @DisplayName("Deve retornar página com usuários cadastrados")
    void deveListarUsuariosComSucesso() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        Pageable pageable = of(0, 10);
        Page<Usuario> pagina = new PageImpl<>(List.of(usuario));
        ListarUsuarioResponse expectedResponse = ListarUsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .imagemPerfil(usuario.getImagemPerfil())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .build();

        when(usuarioRepository.findAll(pageable)).thenReturn(pagina);
        when(listarUsuarioMapper.toResponse(usuario)).thenReturn(expectedResponse);

        // Act
        Page<ListarUsuarioResponse> resultado = service.listar(pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(expectedResponse.id(), resultado.getContent().get(0).id());
        assertEquals(expectedResponse.email(), resultado.getContent().get(0).email());

        verify(usuarioRepository).findAll(pageable);
        verify(listarUsuarioMapper).toResponse(usuario);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há usuários cadastrados")
    void deveRetornarPaginaVaziaQuandoNaoHaUsuarios() {

        // Arrange
        Pageable pageable = of(0, 10);
        Page<Usuario> paginaVazia = empty();

        when(usuarioRepository.findAll(pageable)).thenReturn(paginaVazia);

        // Act
        Page<ListarUsuarioResponse> resultado = service.listar(pageable);

        // Assert
        assertEquals(0, resultado.getTotalElements());
        verify(usuarioRepository).findAll(pageable);
    }
}
