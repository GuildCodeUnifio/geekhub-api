package br.com.guildcode.geekhub.service.anuncio.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
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
class AnuncioSemPedidosValidatorServiceTest {

    @InjectMocks
    private AnuncioSemPedidosValidatorService validatorService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Deve lançar ConflitoException quando anúncio possuir pedidos vinculados")
    void deveLancarExcecaoQuandoAnuncioPossuiPedidos() {

        // Arrange
        when(pedidoRepository.existsByAnuncioId(1L)).thenReturn(TRUE);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> validatorService.validar(1L)
        );

        assertEquals("Não é possível remover anúncio pois existem pedidos vinculados", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando anúncio não possuir pedidos vinculados")
    void naoDeveLancarExcecaoQuandoAnuncioSemPedidos() {

        // Arrange
        when(pedidoRepository.existsByAnuncioId(5L)).thenReturn(FALSE);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar(5L));
    }
}
