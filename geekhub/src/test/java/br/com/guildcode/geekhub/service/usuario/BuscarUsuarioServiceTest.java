package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarUsuarioServiceTest {

    @InjectMocks
    private BuscarUsuarioService service;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve retornar usuário quando e-mail encontrado no repositório")
    void deveBuscarUsuarioPorEmail() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(of(usuario));

        // Act
        Optional<Usuario> resultado = service.buscarPorEmail(usuario.getEmail());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuario.getEmail(), resultado.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando e-mail não existe no repositório")
    void deveRetornarVazioQuandoEmailNaoEncontrado() {

        // Arrange
        when(usuarioRepository.findByEmail("inexistente@email.com")).thenReturn(empty());

        // Act
        Optional<Usuario> resultado = service.buscarPorEmail("inexistente@email.com");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar usuário quando ID encontrado no repositório")
    void deveBuscarUsuarioPorId() {

        // Arrange
        Usuario usuario = UsuarioFactory.get();
        when(usuarioRepository.findById(usuario.getId())).thenReturn(of(usuario));

        // Act
        Usuario resultado = service.buscarPorId(usuario.getId());

        // Assert
        assertEquals(usuario.getId(), resultado.getId());
        assertEquals(usuario.getEmail(), resultado.getEmail());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando ID não encontrado no repositório")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {

        // Arrange
        when(usuarioRepository.findById(99L)).thenReturn(empty());

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}
