package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverUsuarioServiceTest {

    @InjectMocks
    private RemoverUsuarioService service;

    @Mock
    private BuscarUsuarioService buscarUsuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve remover usuário encontrado por ID")
    void deveRemoverUsuarioComSucesso() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        when(buscarUsuarioService.buscarPorId(usuario.getId())).thenReturn(usuario);

        // Act
        service.remover(usuario.getId());

        // Assert
        verify(buscarUsuarioService).buscarPorId(usuario.getId());
        verify(usuarioRepository).delete(usuario);
    }
}
