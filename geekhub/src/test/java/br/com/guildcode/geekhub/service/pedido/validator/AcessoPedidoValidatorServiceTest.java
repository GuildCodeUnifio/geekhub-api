package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

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
}
