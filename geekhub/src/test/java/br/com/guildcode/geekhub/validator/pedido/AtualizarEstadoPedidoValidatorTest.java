package br.com.guildcode.geekhub.validator.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.request.AtualizarEstadoPedidoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.pedido.AtualizarEstadoPedidoRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AtualizarEstadoPedidoValidatorTest {

    private final AtualizarEstadoPedidoValidator validator = new AtualizarEstadoPedidoValidator();

    @Test
    @DisplayName("Deve lançar NegocioException quando requisição de atualização for nula")
    void deveLancarExcecaoQuandoRequestNulo() {
        // Arrange & Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosAtualizacao(null)
        );

        assertEquals("Dados de atualização do pedido são obrigatórios", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando estado informado na requisição for nulo")
    void deveLancarExcecaoQuandoEstadoNulo() {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(null);

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> validator.validarDadosAtualizacao(request)
        );

        assertEquals("O estado do pedido é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando requisição contiver estado válido")
    void naoDeveLancarExcecaoQuandoDadosValidos() {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarDadosAtualizacao(request));
    }
}
