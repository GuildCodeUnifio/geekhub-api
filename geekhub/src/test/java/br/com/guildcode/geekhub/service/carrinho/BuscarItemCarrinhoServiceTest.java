package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarItemCarrinhoServiceTest {

    @InjectMocks
    private BuscarItemCarrinhoService service;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Test
    @DisplayName("Deve retornar item do carrinho quando ID encontrado no repositório")
    void deveBuscarItemCarrinhoPorId() {

        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        when(itemCarrinhoRepository.findById(item.getId())).thenReturn(of(item));

        // Act
        ItemCarrinho resultado = service.buscarPorId(item.getId());

        // Assert
        assertEquals(item.getId(), resultado.getId());
        assertEquals(item.getQuantidade(), resultado.getQuantidade());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando item do carrinho não encontrado por ID")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {

        // Arrange
        when(itemCarrinhoRepository.findById(99L)).thenReturn(empty());

        // Act
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        // Assert
        assertEquals("Item do carrinho não encontrado", ex.getMessage());
    }
}
