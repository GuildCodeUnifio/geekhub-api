package br.com.guildcode.geekhub.service.carrinho.validator;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProprietarioItemCarrinhoValidatorServiceTest {

    private final ProprietarioItemCarrinhoValidatorService validator = new ProprietarioItemCarrinhoValidatorService();

    @Test
    @DisplayName("Deve permitir acesso quando item pertencer ao comprador autenticado")
    void devePermitirAcessoQuandoItemPertencerAoCompradorAutenticado() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        Usuario usuarioAutenticado = UsuarioFactory.get();

        // Act & Assert
        assertDoesNotThrow(() -> validator.validarProprietario(item, usuarioAutenticado));
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando item pertencer a outro comprador")
    void deveLancarAccessDeniedExceptionQuandoItemPertencerAOutroComprador() {
        // Arrange
        ItemCarrinho item = ItemCarrinhoFactory.get();
        Usuario outroUsuario = UsuarioFactory.get();
        outroUsuario.setId(99L);

        // Act
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> validator.validarProprietario(item, outroUsuario));

        // Assert
        assertEquals("Você não possui permissão para acessar este item do carrinho", exception.getMessage());
    }
}
