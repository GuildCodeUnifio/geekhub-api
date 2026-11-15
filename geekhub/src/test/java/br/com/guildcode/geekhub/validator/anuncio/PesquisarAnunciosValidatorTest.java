package br.com.guildcode.geekhub.validator.anuncio;

import br.com.guildcode.geekhub.exception.NegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PesquisarAnunciosValidatorTest {

    private final PesquisarAnunciosValidator validator = new PesquisarAnunciosValidator();

    @Test
    @DisplayName("Deve permitir pesquisa sem faixa de preço informada")
    void devePermitirSemFaixaDePreco() {

        // Arrange & Act & Assert
        assertDoesNotThrow(() -> validator.validarFaixaPreco(null, null));
    }

    @Test
    @DisplayName("Deve permitir apenas preço mínimo igual ou maior que zero")
    void devePermitirApenasPrecoMinimoValido() {

        // Arrange & Act & Assert
        assertDoesNotThrow(() -> validator.validarFaixaPreco(BigDecimal.ZERO, null));
    }

    @Test
    @DisplayName("Deve permitir apenas preço máximo igual ou maior que zero")
    void devePermitirApenasPrecoMaximoValido() {

        // Arrange & Act & Assert
        assertDoesNotThrow(() -> validator.validarFaixaPreco(null, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Deve permitir faixa com preço mínimo menor ou igual ao preço máximo")
    void devePermitirFaixaDePrecoValida() {

        // Arrange & Act & Assert
        assertDoesNotThrow(() -> validator.validarFaixaPreco(BigDecimal.valueOf(100), BigDecimal.valueOf(100)));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando preço mínimo for negativo")
    void deveRejeitarPrecoMinimoNegativo() {

        // Arrange
        BigDecimal precoMin = BigDecimal.valueOf(-1);

        // Act & Assert
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validarFaixaPreco(precoMin, null));

        assertEquals("Preço mínimo não pode ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando preço máximo for negativo")
    void deveRejeitarPrecoMaximoNegativo() {

        // Arrange
        BigDecimal precoMax = BigDecimal.valueOf(-0.01);

        // Act & Assert
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validarFaixaPreco(null, precoMax));

        assertEquals("Preço máximo não pode ser negativo", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando preço mínimo for maior que o preço máximo")
    void deveRejeitarPrecoMinimoMaiorQuePrecoMaximo() {

        // Arrange
        BigDecimal precoMin = BigDecimal.valueOf(300);
        BigDecimal precoMax = BigDecimal.valueOf(100);

        // Act & Assert
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> validator.validarFaixaPreco(precoMin, precoMax));

        assertEquals("Preço mínimo não pode ser maior que o preço máximo", exception.getMessage());
    }
}
