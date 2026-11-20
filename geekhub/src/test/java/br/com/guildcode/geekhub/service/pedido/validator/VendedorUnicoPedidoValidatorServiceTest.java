package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VendedorUnicoPedidoValidatorServiceTest {

    private final VendedorUnicoPedidoValidatorService validatorService = new VendedorUnicoPedidoValidatorService();

    @Test
    @DisplayName("Deve permitir criar pedido com múltiplos anúncios diferentes do mesmo vendedor")
    void devePermitirItensDiferentesDoMesmoVendedor() {
        // Arrange
        ItemCarrinho item1 = ItemCarrinhoFactory.get();

        Anuncio outroAnuncioMesmoVendedor = AnuncioFactory.get();
        outroAnuncioMesmoVendedor.setId(2L);

        ItemCarrinho item2 = ItemCarrinhoFactory.get();
        item2.setId(2L);
        item2.setAnuncio(outroAnuncioMesmoVendedor);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar(List.of(item1, item2)));
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando pedido possuir itens de vendedores diferentes")
    void deveLancarConflitoQuandoHouverItensDeVendedoresDiferentes() {
        // Arrange
        ItemCarrinho item1 = ItemCarrinhoFactory.get();

        Usuario outroVendedor = UsuarioFactory.get();
        outroVendedor.setId(3L);

        Anuncio anuncioOutroVendedor = AnuncioFactory.get();
        anuncioOutroVendedor.setId(2L);
        anuncioOutroVendedor.setVendedor(outroVendedor);

        ItemCarrinho item2 = ItemCarrinhoFactory.get();
        item2.setId(2L);
        item2.setAnuncio(anuncioOutroVendedor);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> validatorService.validar(List.of(item1, item2))
        );

        assertEquals("Todos os itens do pedido devem pertencer ao mesmo vendedor", ex.getMessage());
    }
}
