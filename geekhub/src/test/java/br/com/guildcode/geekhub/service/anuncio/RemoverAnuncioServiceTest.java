package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.anuncio.validator.AnuncioSemPedidosValidatorService;
import br.com.guildcode.geekhub.service.anuncio.validator.ProprietarioAnuncioValidatorService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverAnuncioServiceTest {

    @InjectMocks
    private RemoverAnuncioService service;

    @Mock
    private BuscarAnuncioService buscarAnuncioService;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private ProprietarioAnuncioValidatorService proprietarioAnuncioValidatorService;

    @Mock
    private AnuncioSemPedidosValidatorService anuncioSemPedidosValidatorService;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Test
    @DisplayName("Deve remover anúncio quando usuário autenticado for proprietário e não houver pedidos vinculados")
    void deveRemoverAnuncioComoProprietario() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario proprietario = anuncio.getVendedor();

        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(usuarioAutenticadoService.get()).thenReturn(proprietario);
        doNothing().when(proprietarioAnuncioValidatorService).validarProprietarioOuAdmin(anuncio, proprietario);
        doNothing().when(anuncioSemPedidosValidatorService).validar(anuncio.getId());

        // Act
        service.remover(anuncio.getId());

        // Assert
        verify(proprietarioAnuncioValidatorService).validarProprietarioOuAdmin(anuncio, proprietario);
        verify(anuncioSemPedidosValidatorService).validar(anuncio.getId());
        verify(anuncioRepository).delete(anuncio);
    }

    @Test
    @DisplayName("Deve remover anúncio quando usuário autenticado for ADMIN e não houver pedidos vinculados")
    void deveRemoverAnuncioComoAdmin() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario admin = UsuarioFactory.getAdmin();

        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(usuarioAutenticadoService.get()).thenReturn(admin);
        doNothing().when(proprietarioAnuncioValidatorService).validarProprietarioOuAdmin(anuncio, admin);
        doNothing().when(anuncioSemPedidosValidatorService).validar(anuncio.getId());

        // Act
        service.remover(anuncio.getId());

        // Assert
        verify(proprietarioAnuncioValidatorService).validarProprietarioOuAdmin(anuncio, admin);
        verify(anuncioSemPedidosValidatorService).validar(anuncio.getId());
        verify(anuncioRepository).delete(anuncio);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando terceiro sem perfil ADMIN tentar remover anúncio")
    void deveBloquearTerceiroNaRemocao() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario terceiro = UsuarioFactory.get();

        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(usuarioAutenticadoService.get()).thenReturn(terceiro);
        doThrow(new AccessDeniedException("Você não possui permissão para remover este anúncio"))
                .when(proprietarioAnuncioValidatorService).validarProprietarioOuAdmin(anuncio, terceiro);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.remover(anuncio.getId())
        );

        assertEquals("Você não possui permissão para remover este anúncio", ex.getMessage());
        verifyNoInteractions(anuncioSemPedidosValidatorService);
        verify(anuncioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao tentar remover anúncio com pedidos vinculados")
    void deveLancarConflitoQuandoAnuncioPossuiPedidos() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario proprietario = anuncio.getVendedor();

        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(usuarioAutenticadoService.get()).thenReturn(proprietario);
        doNothing().when(proprietarioAnuncioValidatorService).validarProprietarioOuAdmin(anuncio, proprietario);
        doThrow(new ConflitoException("Não é possível remover anúncio pois existem pedidos vinculados"))
                .when(anuncioSemPedidosValidatorService).validar(anuncio.getId());

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.remover(anuncio.getId())
        );

        assertEquals("Não é possível remover anúncio pois existem pedidos vinculados", ex.getMessage());
        verify(anuncioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar remover anúncio inexistente")
    void deveLancarNotFoundQuandoAnuncioInexistente() {

        // Arrange
        when(buscarAnuncioService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Anúncio não encontrado"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.remover(99L)
        );

        assertEquals("Anúncio não encontrado", ex.getMessage());
        verifyNoInteractions(anuncioSemPedidosValidatorService);
        verify(anuncioRepository, never()).delete(any());
    }
}
