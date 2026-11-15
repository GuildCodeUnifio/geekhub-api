package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LimparCarrinhoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @InjectMocks
    private LimparCarrinhoService service;

    @Test
    @DisplayName("Deve remover todos os itens do carrinho do comprador autenticado")
    void deveLimparTodosOsItensDoCompradorAutenticado() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        when(usuarioAutenticadoService.get()).thenReturn(comprador);

        // Act
        service.limpar();

        // Assert
        verify(itemCarrinhoRepository).deleteByCompradorId(1L);
    }
}
