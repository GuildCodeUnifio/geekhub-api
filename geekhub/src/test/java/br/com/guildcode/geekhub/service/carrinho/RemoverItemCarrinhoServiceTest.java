package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ProprietarioItemCarrinhoValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverItemCarrinhoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarItemCarrinhoService buscarItemCarrinhoService;

    @Mock
    private ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @InjectMocks
    private RemoverItemCarrinhoService service;

    @Test
    @DisplayName("Deve remover item específico do carrinho do comprador autenticado")
    void deveRemoverItemDoCarrinhoComSucesso() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(1L)).thenReturn(item);

        // Act
        service.remover(1L);

        // Assert
        verify(proprietarioItemCarrinhoValidatorService).validarProprietario(item, comprador);
        verify(itemCarrinhoRepository).delete(item);
    }

    @Test
    @DisplayName("Deve propagar AccessDeniedException ao tentar remover item de outro comprador")
    void devePropagarAccessDeniedExceptionAoRemoverItemDeOutroComprador() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(1L)).thenReturn(item);
        doThrow(new AccessDeniedException("Você não possui permissão para acessar este item do carrinho"))
                .when(proprietarioItemCarrinhoValidatorService)
                .validarProprietario(item, comprador);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> service.remover(1L));
        verifyNoInteractions(itemCarrinhoRepository);
    }

    @Test
    @DisplayName("Deve propagar RecursoNaoEncontradoException ao tentar remover item inexistente")
    void devePropagarRecursoNaoEncontradoExceptionAoRemoverItemInexistente() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> service.remover(99L));
        verifyNoInteractions(proprietarioItemCarrinhoValidatorService, itemCarrinhoRepository);
    }
}
