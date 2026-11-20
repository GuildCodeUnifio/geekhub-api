package br.com.guildcode.geekhub.service.carrinho.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendedorUnicoCarrinhoValidatorServiceTest {

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @InjectMocks
    private VendedorUnicoCarrinhoValidatorService validatorService;

    @Test
    @DisplayName("Deve permitir adicionar item quando carrinho estiver vazio ou só contiver itens do mesmo vendedor")
    void devePermitirQuandoMesmoVendedorOuVazio() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();

        when(itemCarrinhoRepository.existsByCompradorIdAndAnuncioVendedorIdNot(
                comprador.getId(), anuncio.getVendedor().getId()))
                .thenReturn(FALSE);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar(comprador, anuncio));
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando carrinho já possuir itens de outro vendedor")
    void deveLancarConflitoQuandoHouverItemDeOutroVendedor() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();

        when(itemCarrinhoRepository.existsByCompradorIdAndAnuncioVendedorIdNot(
                comprador.getId(), anuncio.getVendedor().getId()))
                .thenReturn(TRUE);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> validatorService.validar(comprador, anuncio)
        );

        assertEquals(
                "O carrinho já possui itens de outro vendedor. Finalize ou esvazie o carrinho antes de adicionar produtos de um vendedor diferente",
                ex.getMessage());
    }
}
