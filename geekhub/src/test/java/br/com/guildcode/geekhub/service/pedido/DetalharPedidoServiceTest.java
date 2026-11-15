package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.DetalharPedidoResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.pedido.DetalharPedidoMapper;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.pedido.validator.AcessoPedidoValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalharPedidoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarPedidoService buscarPedidoService;

    @Mock
    private AcessoPedidoValidatorService acessoPedidoValidatorService;

    @Mock
    private DetalharPedidoMapper detalharPedidoMapper;

    @InjectMocks
    private DetalharPedidoService service;

    @Test
    @DisplayName("Deve detalhar pedido após validar permissão de acesso")
    void deveDetalharPedidoComSucesso() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        Pedido pedido = PedidoFactory.get();
        DetalharPedidoResponse responseEsperada = DetalharPedidoResponse.builder()
                .id(pedido.getId())
                .compradorId(comprador.getId())
                .compradorNome(pedido.getCompradorNome())
                .compradorEmail(pedido.getCompradorEmail())
                .itens(Collections.emptyList())
                .valorTotal(pedido.getValorTotal())
                .estado(pedido.getEstado())
                .historico(Collections.emptyList())
                .avaliado(pedido.getAvaliado())
                .criadoEm(pedido.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarPedidoService.buscarPorId(1L)).thenReturn(pedido);
        when(detalharPedidoMapper.toResponse(pedido)).thenReturn(responseEsperada);

        // Act
        DetalharPedidoResponse resultado = service.detalhar(1L);

        // Assert
        assertEquals(responseEsperada, resultado);
        verify(acessoPedidoValidatorService).validarVisualizacao(pedido, comprador);
        verify(detalharPedidoMapper).toResponse(pedido);
    }

    @Test
    @DisplayName("Deve propagar AccessDeniedException quando usuário não tiver permissão para visualizar pedido")
    void deveNegarAcessoQuandoTerceiroTentarDetalhar() {
        // Arrange
        Usuario terceiro = UsuarioFactory.get();
        terceiro.setId(50L);
        Pedido pedido = PedidoFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(terceiro);
        when(buscarPedidoService.buscarPorId(1L)).thenReturn(pedido);
        doThrow(new AccessDeniedException("Você não possui permissão para visualizar este pedido"))
                .when(acessoPedidoValidatorService)
                .validarVisualizacao(pedido, terceiro);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> service.detalhar(1L));
        verifyNoInteractions(detalharPedidoMapper);
    }

    @Test
    @DisplayName("Deve propagar RecursoNaoEncontradoException quando pedido não existir")
    void deveLancarNotFoundQuandoPedidoInexistente() {
        // Arrange
        Usuario usuario = UsuarioFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(usuario);
        when(buscarPedidoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> service.detalhar(99L));
        verifyNoInteractions(acessoPedidoValidatorService, detalharPedidoMapper);
    }
}
