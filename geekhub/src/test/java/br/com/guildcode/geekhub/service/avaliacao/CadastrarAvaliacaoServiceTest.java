package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.CadastrarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.factory.avaliacao.CadastrarAvaliacaoRequestFactory;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.avaliacao.CadastrarAvaliacaoMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.avaliacao.validator.ElegibilidadeAvaliacaoValidatorService;
import br.com.guildcode.geekhub.service.pedido.BuscarPedidoService;
import br.com.guildcode.geekhub.service.usuario.BuscarUsuarioService;
import br.com.guildcode.geekhub.validator.avaliacao.CadastrarAvaliacaoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarAvaliacaoServiceTest {

    @Mock
    private CadastrarAvaliacaoValidator cadastrarAvaliacaoValidator;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarPedidoService buscarPedidoService;

    @Mock
    private ElegibilidadeAvaliacaoValidatorService elegibilidadeAvaliacaoValidatorService;

    @Mock
    private BuscarUsuarioService buscarUsuarioService;

    @Mock
    private CadastrarAvaliacaoMapper cadastrarAvaliacaoMapper;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private CadastrarAvaliacaoService service;

    @Test
    @DisplayName("Deve cadastrar avaliação de pedido elegível e marcar pedido como avaliado")
    void deveCadastrarAvaliacaoComSucesso() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        Pedido pedido = PedidoFactory.get();
        pedido.setId(request.pedidoId());
        pedido.setEstado(CONCLUIDO);
        pedido.setAvaliado(FALSE);

        Usuario autor = pedido.getComprador();
        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(pedido.obterVendedorId());

        Avaliacao avaliacaoMapeada = AvaliacaoFactory.get();
        avaliacaoMapeada.setId(null);
        Avaliacao avaliacaoSalva = AvaliacaoFactory.get();

        CadastrarAvaliacaoResponse expectedResponse = CadastrarAvaliacaoResponse.builder()
                .id(avaliacaoSalva.getId())
                .pedidoId(pedido.getId())
                .vendedorId(vendedor.getId())
                .autorId(autor.getId())
                .autorNome(autor.getNome())
                .nota(request.nota())
                .comentario(request.comentario())
                .criadoEm(avaliacaoSalva.getCriadoEm())
                .build();

        doNothing().when(cadastrarAvaliacaoValidator).validarDadosCadastro(request);
        when(usuarioAutenticadoService.get()).thenReturn(autor);
        when(buscarPedidoService.buscarPorId(request.pedidoId())).thenReturn(pedido);
        doNothing().when(elegibilidadeAvaliacaoValidatorService).validar(pedido, autor);
        when(buscarUsuarioService.buscarPorId(pedido.obterVendedorId())).thenReturn(vendedor);
        when(cadastrarAvaliacaoMapper.toEntity(eq(request), eq(pedido), eq(autor), eq(vendedor), any(LocalDateTime.class)))
                .thenReturn(avaliacaoMapeada);
        when(avaliacaoRepository.save(avaliacaoMapeada)).thenReturn(avaliacaoSalva);
        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        when(cadastrarAvaliacaoMapper.toResponse(avaliacaoSalva)).thenReturn(expectedResponse);

        // Act
        CadastrarAvaliacaoResponse response = service.cadastrar(request);

        // Assert
        assertEquals(expectedResponse, response);
        assertTrue(pedido.getAvaliado());
        verify(cadastrarAvaliacaoValidator).validarDadosCadastro(request);
        verify(usuarioAutenticadoService).get();
        verify(buscarPedidoService).buscarPorId(request.pedidoId());
        verify(elegibilidadeAvaliacaoValidatorService).validar(pedido, autor);
        verify(buscarUsuarioService).buscarPorId(pedido.obterVendedorId());
        verify(avaliacaoRepository).save(avaliacaoMapeada);
        verify(pedidoRepository).save(pedido);
        verify(cadastrarAvaliacaoMapper).toResponse(avaliacaoSalva);
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando dados de cadastro da avaliação forem inválidos")
    void deveLancarNegocioExceptionQuandoDadosInvalidos() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequest.builder()
                .pedidoId(3L)
                .nota(6)
                .build();
        doThrow(new NegocioException("A nota da avaliação deve ser entre 1 e 5"))
                .when(cadastrarAvaliacaoValidator)
                .validarDadosCadastro(request);

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> service.cadastrar(request)
        );

        assertEquals("A nota da avaliação deve ser entre 1 e 5", ex.getMessage());
        verifyNoInteractions(
                usuarioAutenticadoService,
                buscarPedidoService,
                elegibilidadeAvaliacaoValidatorService,
                buscarUsuarioService,
                cadastrarAvaliacaoMapper,
                avaliacaoRepository,
                pedidoRepository
        );
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando pedido informado não existir")
    void deveLancarRecursoNaoEncontradoQuandoPedidoNaoExistir() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        Usuario autor = UsuarioFactory.get();

        doNothing().when(cadastrarAvaliacaoValidator).validarDadosCadastro(request);
        when(usuarioAutenticadoService.get()).thenReturn(autor);
        when(buscarPedidoService.buscarPorId(request.pedidoId()))
                .thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.cadastrar(request)
        );

        assertEquals("Pedido não encontrado", ex.getMessage());
        verifyNoInteractions(
                elegibilidadeAvaliacaoValidatorService,
                buscarUsuarioService,
                cadastrarAvaliacaoMapper,
                avaliacaoRepository,
                pedidoRepository
        );
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando pedido não for elegível por já possuir avaliação")
    void deveLancarConflitoQuandoPedidoNaoForElegivel() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        Pedido pedido = PedidoFactory.get();
        Usuario autor = pedido.getComprador();

        doNothing().when(cadastrarAvaliacaoValidator).validarDadosCadastro(request);
        when(usuarioAutenticadoService.get()).thenReturn(autor);
        when(buscarPedidoService.buscarPorId(request.pedidoId())).thenReturn(pedido);
        doThrow(new ConflitoException("Este pedido já foi avaliado"))
                .when(elegibilidadeAvaliacaoValidatorService)
                .validar(pedido, autor);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.cadastrar(request)
        );

        assertEquals("Este pedido já foi avaliado", ex.getMessage());
        verifyNoInteractions(
                buscarUsuarioService,
                cadastrarAvaliacaoMapper,
                avaliacaoRepository,
                pedidoRepository
        );
    }
}
