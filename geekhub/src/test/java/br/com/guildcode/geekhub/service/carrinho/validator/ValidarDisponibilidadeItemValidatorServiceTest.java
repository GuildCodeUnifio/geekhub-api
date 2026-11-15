package br.com.guildcode.geekhub.service.carrinho.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidarDisponibilidadeItemValidatorServiceTest {

    private final ValidarDisponibilidadeItemValidatorService validator = new ValidarDisponibilidadeItemValidatorService();

    @Test
    @DisplayName("Deve validar disponibilidade do item quando anúncio e quantidade forem válidos")
    void deveValidarDisponibilidadeQuandoAnuncioEQuantidadeForemValidos() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario comprador = UsuarioFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validar(anuncio, comprador, 2));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando anúncio estiver indisponível")
    void deveLancarNegocioExceptionQuandoAnuncioEstiverIndisponivel() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setDisponivel(FALSE);
        Usuario comprador = UsuarioFactory.get();

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validar(anuncio, comprador, 1));

        // Assert
        assertEquals("Anúncio indisponível ou sem estoque para compra", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando disponibilidade do anúncio for nula")
    void deveLancarNegocioExceptionQuandoDisponibilidadeForNula() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setDisponivel(null);
        Usuario comprador = UsuarioFactory.get();

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validar(anuncio, comprador, 1));

        // Assert
        assertEquals("Anúncio indisponível ou sem estoque para compra", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando estoque disponível for zero")
    void deveLancarNegocioExceptionQuandoEstoqueForZero() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setQuantidadeDisponivel(0);
        Usuario comprador = UsuarioFactory.get();

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validar(anuncio, comprador, 1));

        // Assert
        assertEquals("Anúncio indisponível ou sem estoque para compra", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando comprador tentar adicionar o próprio anúncio")
    void deveLancarNegocioExceptionQuandoCompradorForProprioVendedor() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario comprador = UsuarioFactory.get();
        comprador.setId(anuncio.getVendedor().getId());

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validar(anuncio, comprador, 1));

        // Assert
        assertEquals("Você não pode adicionar o próprio anúncio ao carrinho", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade total exceder o estoque disponível")
    void deveLancarNegocioExceptionQuandoQuantidadeExcederEstoque() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario comprador = UsuarioFactory.get();

        // Act
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validar(anuncio, comprador, 4));

        // Assert
        assertEquals("Quantidade solicitada excede o estoque disponível do anúncio", exception.getMessage());
    }
}
