package br.com.guildcode.geekhub.domain.carrinho;

import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemCarrinhoTest {

    @Test
    @DisplayName("Deve calcular subtotal multiplicando preço unitário pela quantidade com duas casas decimais")
    void deveCalcularSubtotalCorretamente() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        item.getAnuncio().setPreco(new BigDecimal("49.90"));
        item.setQuantidade(3);

        // Act
        BigDecimal subtotal = item.calcularSubtotal();

        // Assert
        assertEquals(new BigDecimal("149.70"), subtotal);
    }

    @Test
    @DisplayName("Deve aplicar arredondamento HALF_UP ao calcular subtotal")
    void deveAplicarArredondamentoHalfUpNoSubtotal() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        item.getAnuncio().setPreco(new BigDecimal("10.005"));
        item.setQuantidade(1);

        // Act
        BigDecimal subtotal = item.calcularSubtotal();

        // Assert
        assertEquals(new BigDecimal("10.01"), subtotal);
    }
}
