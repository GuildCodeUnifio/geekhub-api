package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.ENVIADO;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransicaoEstadoPedidoValidatorServiceTest {

    private final TransicaoEstadoPedidoValidatorService validatorService = new TransicaoEstadoPedidoValidatorService();

    @Test
    @DisplayName("Deve permitir todas as transições válidas da máquina de estados do pedido")
    void devePermitirTransicoesValidas() {
        // Arrange
        List<EstadoPedido[]> transicoesValidas = List.of(
                new EstadoPedido[]{CRIADO, CONFIRMADO},
                new EstadoPedido[]{CRIADO, CANCELADO},
                new EstadoPedido[]{CONFIRMADO, ENVIADO},
                new EstadoPedido[]{CONFIRMADO, CANCELADO},
                new EstadoPedido[]{ENVIADO, CONCLUIDO}
        );

        // Act & Assert
        for (EstadoPedido[] transicao : transicoesValidas) {
            assertDoesNotThrow(() -> validatorService.validar(transicao[0], transicao[1]));
        }
    }

    @Test
    @DisplayName("Deve lançar NegocioException para todos os saltos, repetições e regressões inválidas em estados ativos")
    void deveBloquearTransicoesInvalidasEmEstadosAtivos() {
        // Arrange
        List<EstadoPedido[]> transicoesInvalidas = List.of(
                new EstadoPedido[]{CRIADO, CRIADO},
                new EstadoPedido[]{CRIADO, ENVIADO},
                new EstadoPedido[]{CRIADO, CONCLUIDO},
                new EstadoPedido[]{CONFIRMADO, CRIADO},
                new EstadoPedido[]{CONFIRMADO, CONFIRMADO},
                new EstadoPedido[]{CONFIRMADO, CONCLUIDO},
                new EstadoPedido[]{ENVIADO, CRIADO},
                new EstadoPedido[]{ENVIADO, CONFIRMADO},
                new EstadoPedido[]{ENVIADO, ENVIADO},
                new EstadoPedido[]{ENVIADO, CANCELADO}
        );

        // Act & Assert
        for (EstadoPedido[] transicao : transicoesInvalidas) {
            NegocioException ex = assertThrows(
                    NegocioException.class,
                    () -> validatorService.validar(transicao[0], transicao[1])
            );
            assertEquals(
                    String.format("Transição de estado inválida de %s para %s", transicao[0], transicao[1]),
                    ex.getMessage());
        }
    }

    @Test
    @DisplayName("Deve lançar NegocioException para qualquer tentativa de alteração em pedido CONCLUIDO ou CANCELADO")
    void deveBloquearAlteracaoEmPedidosFinalizados() {
        // Arrange
        List<EstadoPedido> estadosFinais = List.of(CONCLUIDO, CANCELADO);

        // Act & Assert
        for (EstadoPedido estadoFinal : estadosFinais) {
            for (EstadoPedido destino : EstadoPedido.values()) {
                NegocioException ex = assertThrows(
                        NegocioException.class,
                        () -> validatorService.validar(estadoFinal, destino)
                );
                assertEquals("Não é possível alterar um pedido que já está " + estadoFinal, ex.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando estado atual ou novo estado for nulo")
    void deveLancarExcecaoQuandoEstadoAtualOuNovoEstadoForNulo() {
        // Act & Assert
        NegocioException exAtualNulo = assertThrows(
                NegocioException.class,
                () -> validatorService.validar(null, CONFIRMADO)
        );
        NegocioException exNovoNulo = assertThrows(
                NegocioException.class,
                () -> validatorService.validar(CRIADO, null)
        );

        assertEquals("O estado do pedido é obrigatório", exAtualNulo.getMessage());
        assertEquals("O estado do pedido é obrigatório", exNovoNulo.getMessage());
    }
}
