package br.com.guildcode.geekhub.service.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.PerfilVendedorResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.usuario.PerfilVendedorMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilVendedorServiceTest {

    @Mock
    private BuscarUsuarioService buscarUsuarioService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PerfilVendedorMapper perfilVendedorMapper;

    @InjectMocks
    private PerfilVendedorService service;

    @Test
    @DisplayName("Deve consultar perfil do vendedor com nota média arredondada e contagens reais")
    void deveConsultarPerfilVendedorComNotaMediaArredondadaEContagensReais() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario vendedor = anuncio.getVendedor();
        Avaliacao avaliacao = AvaliacaoFactory.get();
        List<Avaliacao> avaliacoes = List.of(avaliacao);
        List<Anuncio> anuncios = List.of(anuncio);

        PerfilVendedorResponse expectedResponse = PerfilVendedorResponse.builder()
                .id(vendedor.getId())
                .nome(vendedor.getNome())
                .imagemPerfil(vendedor.getImagemPerfil())
                .membroDesde(vendedor.getCriadoEm())
                .notaMedia(4.67)
                .quantidadeAvaliacoes(3L)
                .quantidadeProdutos(2L)
                .quantidadeVendas(5L)
                .avaliacoes(List.of())
                .anuncios(List.of())
                .build();

        when(buscarUsuarioService.buscarPorId(vendedor.getId())).thenReturn(vendedor);
        when(avaliacaoRepository.contarPorVendedorId(vendedor.getId())).thenReturn(3L);
        when(avaliacaoRepository.calcularNotaMediaPorVendedorId(vendedor.getId())).thenReturn(4.666666);
        when(avaliacaoRepository.findByVendedorIdOrderByCriadoEmDesc(vendedor.getId())).thenReturn(avaliacoes);
        when(anuncioRepository.contarDisponiveisPorVendedorId(vendedor.getId())).thenReturn(2L);
        when(anuncioRepository.listarDisponiveisPorVendedorId(vendedor.getId())).thenReturn(anuncios);
        when(pedidoRepository.contarPorVendedorIdEEstado(vendedor.getId(), CONCLUIDO)).thenReturn(5L);
        when(perfilVendedorMapper.toResponse(vendedor, 4.67, 3L, 2L, 5L, avaliacoes, anuncios))
                .thenReturn(expectedResponse);

        // Act
        PerfilVendedorResponse response = service.consultar(vendedor.getId());

        // Assert
        assertEquals(expectedResponse, response);
        assertEquals(4.67, response.notaMedia());
        assertEquals(3L, response.quantidadeAvaliacoes());
        assertEquals(2L, response.quantidadeProdutos());
        assertEquals(5L, response.quantidadeVendas());
        verify(avaliacaoRepository).calcularNotaMediaPorVendedorId(vendedor.getId());
        verify(avaliacaoRepository).findByVendedorIdOrderByCriadoEmDesc(vendedor.getId());
        verify(anuncioRepository).listarDisponiveisPorVendedorId(vendedor.getId());
        verify(pedidoRepository).contarPorVendedorIdEEstado(vendedor.getId(), CONCLUIDO);
    }

    @Test
    @DisplayName("Deve retornar notaMedia nula, contagens zeradas e listas vazias quando vendedor não possuir histórico")
    void deveRetornarNotaMediaNulaEListasVaziasQuandoSemHistorico() {
        // Arrange
        Usuario vendedor = UsuarioFactory.get();
        PerfilVendedorResponse expectedResponse = PerfilVendedorResponse.builder()
                .id(vendedor.getId())
                .nome(vendedor.getNome())
                .imagemPerfil(vendedor.getImagemPerfil())
                .membroDesde(vendedor.getCriadoEm())
                .notaMedia(null)
                .quantidadeAvaliacoes(0L)
                .quantidadeProdutos(0L)
                .quantidadeVendas(0L)
                .avaliacoes(List.of())
                .anuncios(List.of())
                .build();

        when(buscarUsuarioService.buscarPorId(vendedor.getId())).thenReturn(vendedor);
        when(avaliacaoRepository.contarPorVendedorId(vendedor.getId())).thenReturn(0L);
        when(anuncioRepository.contarDisponiveisPorVendedorId(vendedor.getId())).thenReturn(0L);
        when(pedidoRepository.contarPorVendedorIdEEstado(vendedor.getId(), CONCLUIDO)).thenReturn(0L);
        when(perfilVendedorMapper.toResponse(vendedor, null, 0L, 0L, 0L, List.of(), List.of()))
                .thenReturn(expectedResponse);

        // Act
        PerfilVendedorResponse response = service.consultar(vendedor.getId());

        // Assert
        assertNull(response.notaMedia());
        assertEquals(0L, response.quantidadeAvaliacoes());
        assertEquals(0L, response.quantidadeProdutos());
        assertEquals(0L, response.quantidadeVendas());
        verify(avaliacaoRepository, never()).calcularNotaMediaPorVendedorId(vendedor.getId());
        verify(avaliacaoRepository, never()).findByVendedorIdOrderByCriadoEmDesc(vendedor.getId());
        verify(anuncioRepository, never()).listarDisponiveisPorVendedorId(vendedor.getId());
        verify(perfilVendedorMapper).toResponse(vendedor, null, 0L, 0L, 0L, List.of(), List.of());
    }

    @Test
    @DisplayName("Deve retornar notaMedia nula e avaliações vazias quando vendedor possuir apenas anúncios disponíveis")
    void deveConsultarPerfilComAnunciosSemAvaliacoes() {
        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario vendedor = anuncio.getVendedor();
        List<Anuncio> anuncios = List.of(anuncio);

        PerfilVendedorResponse expectedResponse = PerfilVendedorResponse.builder()
                .id(vendedor.getId())
                .nome(vendedor.getNome())
                .imagemPerfil(vendedor.getImagemPerfil())
                .membroDesde(vendedor.getCriadoEm())
                .notaMedia(null)
                .quantidadeAvaliacoes(0L)
                .quantidadeProdutos(1L)
                .quantidadeVendas(0L)
                .avaliacoes(List.of())
                .anuncios(List.of())
                .build();

        when(buscarUsuarioService.buscarPorId(vendedor.getId())).thenReturn(vendedor);
        when(avaliacaoRepository.contarPorVendedorId(vendedor.getId())).thenReturn(0L);
        when(anuncioRepository.contarDisponiveisPorVendedorId(vendedor.getId())).thenReturn(1L);
        when(anuncioRepository.listarDisponiveisPorVendedorId(vendedor.getId())).thenReturn(anuncios);
        when(pedidoRepository.contarPorVendedorIdEEstado(vendedor.getId(), CONCLUIDO)).thenReturn(0L);
        when(perfilVendedorMapper.toResponse(vendedor, null, 0L, 1L, 0L, List.of(), anuncios))
                .thenReturn(expectedResponse);

        // Act
        PerfilVendedorResponse response = service.consultar(vendedor.getId());

        // Assert
        assertNull(response.notaMedia());
        assertEquals(1L, response.quantidadeProdutos());
        verify(avaliacaoRepository, never()).calcularNotaMediaPorVendedorId(vendedor.getId());
        verify(anuncioRepository).listarDisponiveisPorVendedorId(vendedor.getId());
        verify(perfilVendedorMapper).toResponse(vendedor, null, 0L, 1L, 0L, List.of(), anuncios);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando vendedor informado não existir")
    void deveLancarRecursoNaoEncontradoQuandoVendedorNaoExistir() {
        // Arrange
        when(buscarUsuarioService.buscarPorId(99999L))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.consultar(99999L)
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
        verifyNoInteractions(avaliacaoRepository, anuncioRepository, pedidoRepository, perfilVendedorMapper);
    }
}
