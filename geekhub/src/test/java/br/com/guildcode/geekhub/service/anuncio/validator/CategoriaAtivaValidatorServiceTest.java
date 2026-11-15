package br.com.guildcode.geekhub.service.anuncio.validator;

import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CategoriaAtivaValidatorServiceTest {

    private final CategoriaAtivaValidatorService validatorService = new CategoriaAtivaValidatorService();

    @Test
    @DisplayName("Não deve lançar exceção quando categoria estiver ativa")
    void naoDeveLancarExcecaoQuandoCategoriaAtiva() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar(categoria));
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando categoria estiver desativada")
    void deveLancarExcecaoQuandoCategoriaInativa() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        categoria.setAtiva(FALSE);

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validatorService.validar(categoria)
        );

        assertEquals("Não é possível vincular anúncio a uma categoria desativada", ex.getMessage());
    }
}
