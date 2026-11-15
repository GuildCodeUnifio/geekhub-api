package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstoqueDisponivelPedidoValidatorServiceTest {

    private final EstoqueDisponivelPedidoValidatorService validator =
            new EstoqueDisponivelPedidoValidatorService();

    @Test
    @DisplayName("Deve validar disponibilidade e estoque quando todos os itens forem válidos")
    void deveValidarTodosOsItensQuandoDisponiveisEComEstoque() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        List<ItemCarrinho> itens = List.of(item);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validar(itens));
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando anúncio estiver indisponível")
    void deveLancarConflitoExceptionQuandoAnuncioEstiverIndisponivel() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        item.getAnuncio().setDisponivel(FALSE);
        List<ItemCarrinho> itens = List.of(item);

        // Act
        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> validator.validar(itens));

        // Assert
        assertEquals(
                "O anúncio 'Action figure Naruto 1ª edição' não está mais disponível para compra",
                exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando disponibilidade do anúncio for nula")
    void deveLancarConflitoExceptionQuandoDisponibilidadeForNula() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        item.getAnuncio().setDisponivel(null);
        List<ItemCarrinho> itens = List.of(item);

        // Act
        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> validator.validar(itens));

        // Assert
        assertEquals(
                "O anúncio 'Action figure Naruto 1ª edição' não está mais disponível para compra",
                exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando estoque disponível do anúncio for zero")
    void deveLancarConflitoExceptionQuandoEstoqueForZero() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        item.getAnuncio().setQuantidadeDisponivel(0);
        List<ItemCarrinho> itens = List.of(item);

        // Act
        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> validator.validar(itens));

        // Assert
        assertEquals(
                "O anúncio 'Action figure Naruto 1ª edição' não está mais disponível para compra",
                exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando quantidade no carrinho exceder estoque disponível")
    void deveLancarConflitoExceptionQuandoQuantidadeExcederEstoque() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        item.setQuantidade(5);
        List<ItemCarrinho> itens = List.of(item);

        // Act
        ConflitoException exception = assertThrows(
                ConflitoException.class,
                () -> validator.validar(itens));

        // Assert
        assertEquals(
                "Estoque insuficiente para o anúncio 'Action figure Naruto 1ª edição'",
                exception.getMessage());
    }
}
