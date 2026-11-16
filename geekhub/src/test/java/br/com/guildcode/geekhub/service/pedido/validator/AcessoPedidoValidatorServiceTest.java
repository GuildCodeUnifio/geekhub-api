package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.ENVIADO;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcessoPedidoValidatorServiceTest {

    private final AcessoPedidoValidatorService validatorService = new AcessoPedidoValidatorService();

    @Test
    @DisplayName("Deve permitir visualização quando usuário autenticado for o comprador do pedido")
    void devePermitirVisualizacaoParaComprador() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario comprador = pedido.getComprador();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarVisualizacao(pedido, comprador));
    }

    @Test
    @DisplayName("Deve permitir visualização quando usuário autenticado for vendedor participante do pedido")
    void devePermitirVisualizacaoParaVendedorParticipante() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarVisualizacao(pedido, vendedor));
    }

    @Test
    @DisplayName("Deve permitir visualização quando usuário autenticado possuir permissão ADMIN")
    void devePermitirVisualizacaoParaAdmin() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario admin = UsuarioFactory.getAdmin();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarVisualizacao(pedido, admin));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando terceiro sem participação nem ADMIN tentar visualizar pedido")
    void deveBloquearVisualizacaoParaTerceiro() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario terceiro = UsuarioFactory.get();
        terceiro.setId(50L);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> validatorService.validarVisualizacao(pedido, terceiro)
        );

        assertEquals("Você não possui permissão para visualizar este pedido", ex.getMessage());
    }

    @Test
    @DisplayName("Deve permitir que comprador cancele o próprio pedido")
    void devePermitirCancelamentoPeloComprador() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario comprador = pedido.getComprador();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarAtualizacaoEstado(pedido, comprador, CANCELADO));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando comprador tentar avançar estado do próprio pedido")
    void deveBloquearAvancoDeEstadoPeloComprador() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario comprador = pedido.getComprador();

        // Act & Assert
        for (var estadoAvanco : new br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido[]{CONFIRMADO, ENVIADO, CONCLUIDO}) {
            AccessDeniedException ex = assertThrows(
                    AccessDeniedException.class,
                    () -> validatorService.validarAtualizacaoEstado(pedido, comprador, estadoAvanco)
            );
            assertEquals("Você não possui permissão para avançar o estado deste pedido", ex.getMessage());
        }
    }

    @Test
    @DisplayName("Deve permitir que vendedor participante avance ou cancele o estado do pedido")
    void devePermitirAtualizacaoDeEstadoPeloVendedor() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarAtualizacaoEstado(pedido, vendedor, CONFIRMADO));
        assertDoesNotThrow(() -> validatorService.validarAtualizacaoEstado(pedido, vendedor, CANCELADO));
    }

    @Test
    @DisplayName("Deve permitir que administrador avance ou cancele o estado de qualquer pedido")
    void devePermitirAtualizacaoDeEstadoPeloAdmin() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario admin = UsuarioFactory.getAdmin();

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validarAtualizacaoEstado(pedido, admin, CONFIRMADO));
        assertDoesNotThrow(() -> validatorService.validarAtualizacaoEstado(pedido, admin, CANCELADO));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando terceiro sem participação tentar atualizar estado do pedido")
    void deveBloquearAtualizacaoDeEstadoParaTerceiro() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario terceiro = UsuarioFactory.get();
        terceiro.setId(50L);

        // Act & Assert
        AccessDeniedException exAvanco = assertThrows(
                AccessDeniedException.class,
                () -> validatorService.validarAtualizacaoEstado(pedido, terceiro, CONFIRMADO)
        );
        AccessDeniedException exCancelamento = assertThrows(
                AccessDeniedException.class,
                () -> validatorService.validarAtualizacaoEstado(pedido, terceiro, CANCELADO)
        );

        assertEquals("Você não possui permissão para atualizar este pedido", exAvanco.getMessage());
        assertEquals("Você não possui permissão para atualizar este pedido", exCancelamento.getMessage());
    }
}
