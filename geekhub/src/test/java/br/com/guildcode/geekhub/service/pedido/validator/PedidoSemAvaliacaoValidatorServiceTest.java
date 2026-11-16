package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
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
class PedidoSemAvaliacaoValidatorServiceTest {

    @InjectMocks
    private PedidoSemAvaliacaoValidatorService validatorService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Test
    @DisplayName("Deve lançar ConflitoException quando pedido possuir avaliação vinculada")
    void deveLancarConflitoQuandoPedidoPossuiAvaliacao() {
        // Arrange
        when(avaliacaoRepository.existsByPedidoId(4L)).thenReturn(TRUE);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> validatorService.validar(4L)
        );

        assertEquals("Não é possível remover pedido pois possui avaliação vinculada", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando pedido não possuir avaliação vinculada")
    void naoDeveLancarExcecaoQuandoPedidoSemAvaliacao() {
        // Arrange
        when(avaliacaoRepository.existsByPedidoId(1L)).thenReturn(FALSE);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar(1L));
    }
}
