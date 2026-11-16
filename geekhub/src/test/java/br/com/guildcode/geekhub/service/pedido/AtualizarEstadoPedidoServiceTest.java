package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.request.AtualizarEstadoPedidoRequest;
import br.com.guildcode.geekhub.controller.pedido.dto.response.AtualizarEstadoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.EventoPedidoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.pedido.AtualizarEstadoPedidoRequestFactory;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.pedido.AtualizarEstadoPedidoMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.anuncio.BuscarAnuncioService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.pedido.validator.AcessoPedidoValidatorService;
import br.com.guildcode.geekhub.service.pedido.validator.TransicaoEstadoPedidoValidatorService;
import br.com.guildcode.geekhub.validator.pedido.AtualizarEstadoPedidoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarEstadoPedidoServiceTest {

    @Mock
    private AtualizarEstadoPedidoValidator atualizarEstadoPedidoValidator;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarPedidoService buscarPedidoService;

    @Mock
    private AcessoPedidoValidatorService acessoPedidoValidatorService;

    @Mock
    private TransicaoEstadoPedidoValidatorService transicaoEstadoPedidoValidatorService;

    @Mock
    private BuscarAnuncioService buscarAnuncioService;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private AtualizarEstadoPedidoMapper atualizarEstadoPedidoMapper;

    @InjectMocks
    private AtualizarEstadoPedidoService service;

    @Test
    @DisplayName("Deve avançar estado do pedido e registrar evento cronológico sem alterar estoque")
    void deveAvancarEstadoDoPedidoComSucesso() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CONFIRMADO);

        AtualizarEstadoPedidoResponse responseEsperada = AtualizarEstadoPedidoResponse.builder()
                .id(pedido.getId())
                .estado(CONFIRMADO)
                .historico(List.of(
                        EventoPedidoResponse.builder().estado(CRIADO).ocorridoEm(pedido.getCriadoEm()).build(),
                        EventoPedidoResponse.builder().estado(CONFIRMADO).ocorridoEm(LocalDateTime.now()).build()))
                .avaliado(FALSE)
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(vendedor);
        when(buscarPedidoService.buscarPorId(pedido.getId())).thenReturn(pedido);
        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        when(atualizarEstadoPedidoMapper.toResponse(pedido)).thenReturn(responseEsperada);

        // Act
        AtualizarEstadoPedidoResponse resultado = service.atualizar(pedido.getId(), request);

        // Assert
        assertEquals(responseEsperada, resultado);
        assertEquals(CONFIRMADO, pedido.getEstado());
        assertEquals(2, pedido.getHistorico().size());
        assertEquals(CONFIRMADO, pedido.getHistorico().getLast().getEstado());
        verify(atualizarEstadoPedidoValidator).validarDadosAtualizacao(request);
        verify(acessoPedidoValidatorService).validarAtualizacaoEstado(pedido, vendedor, CONFIRMADO);
        verify(transicaoEstadoPedidoValidatorService).validar(CRIADO, CONFIRMADO);
        verify(pedidoRepository).save(pedido);
        verifyNoInteractions(buscarAnuncioService, anuncioRepository);
    }

    @Test
    @DisplayName("Deve cancelar pedido e estornar quantidades ao estoque reativando anúncio esgotado")
    void deveCancelarPedidoEEstornarEstoqueComSucesso() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario comprador = pedido.getComprador();
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CANCELADO);

        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setQuantidadeDisponivel(0);
        anuncio.setDisponivel(FALSE);
        pedido.getItens().getFirst().setQuantidade(2);

        AtualizarEstadoPedidoResponse responseEsperada = AtualizarEstadoPedidoResponse.builder()
                .id(pedido.getId())
                .estado(CANCELADO)
                .historico(List.of(
                        EventoPedidoResponse.builder().estado(CRIADO).ocorridoEm(pedido.getCriadoEm()).build(),
                        EventoPedidoResponse.builder().estado(CANCELADO).ocorridoEm(LocalDateTime.now()).build()))
                .avaliado(FALSE)
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarPedidoService.buscarPorId(pedido.getId())).thenReturn(pedido);
        when(buscarAnuncioService.buscarPorId(pedido.getItens().getFirst().getAnuncioId())).thenReturn(anuncio);
        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        when(atualizarEstadoPedidoMapper.toResponse(pedido)).thenReturn(responseEsperada);

        // Act
        AtualizarEstadoPedidoResponse resultado = service.atualizar(pedido.getId(), request);

        // Assert
        assertEquals(responseEsperada, resultado);
        assertEquals(CANCELADO, pedido.getEstado());
        assertEquals(2, pedido.getHistorico().size());
        assertEquals(CANCELADO, pedido.getHistorico().getLast().getEstado());
        assertEquals(2, anuncio.getQuantidadeDisponivel());
        assertEquals(TRUE, anuncio.getDisponivel());
        verify(anuncioRepository).save(anuncio);
        verify(pedidoRepository).save(pedido);
    }

    @Test
    @DisplayName("Deve impedir atualização quando dados da requisição forem inválidos")
    void deveImpedirAtualizacaoQuandoDadosInvalidos() {
        // Arrange
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(null);
        doThrow(new NegocioException("O estado do pedido é obrigatório"))
                .when(atualizarEstadoPedidoValidator)
                .validarDadosAtualizacao(request);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.atualizar(1L, request));
        verifyNoInteractions(usuarioAutenticadoService, buscarPedidoService,
                acessoPedidoValidatorService, transicaoEstadoPedidoValidatorService,
                buscarAnuncioService, anuncioRepository, pedidoRepository, atualizarEstadoPedidoMapper);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário autenticado não tiver permissão para transição")
    void deveLancarAccessDeniedQuandoSemPermissao() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario comprador = pedido.getComprador();
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CONFIRMADO);

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarPedidoService.buscarPorId(pedido.getId())).thenReturn(pedido);
        doThrow(new AccessDeniedException("Você não possui permissão para avançar o estado deste pedido"))
                .when(acessoPedidoValidatorService)
                .validarAtualizacaoEstado(pedido, comprador, CONFIRMADO);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> service.atualizar(pedido.getId(), request));
        verifyNoInteractions(transicaoEstadoPedidoValidatorService, buscarAnuncioService,
                anuncioRepository, pedidoRepository, atualizarEstadoPedidoMapper);
    }

    @Test
    @DisplayName("Deve lançar NegocioException sem salvar nem alterar estoque quando transição for inválida")
    void deveLancarNegocioExceptionQuandoTransicaoInvalida() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Usuario admin = UsuarioFactory.getAdmin();
        AtualizarEstadoPedidoRequest request = AtualizarEstadoPedidoRequestFactory.get(CONCLUIDO);

        when(usuarioAutenticadoService.get()).thenReturn(admin);
        when(buscarPedidoService.buscarPorId(pedido.getId())).thenReturn(pedido);
        doThrow(new NegocioException("Transição de estado inválida de CRIADO para CONCLUIDO"))
                .when(transicaoEstadoPedidoValidatorService)
                .validar(CRIADO, CONCLUIDO);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.atualizar(pedido.getId(), request));
        assertEquals(CRIADO, pedido.getEstado());
        assertEquals(1, pedido.getHistorico().size());
        verify(pedidoRepository, never()).save(any());
        verifyNoInteractions(buscarAnuncioService, anuncioRepository, atualizarEstadoPedidoMapper);
    }
}
