package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.DetalharAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ResumoVendedorResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.mapper.anuncio.DetalharAnuncioMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalharAnuncioServiceTest {

    @InjectMocks
    private DetalharAnuncioService service;

    @Mock
    private BuscarAnuncioService buscarAnuncioService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private DetalharAnuncioMapper detalharAnuncioMapper;

    @Test
    @DisplayName("Deve detalhar anúncio com nota média arredondada e total de avaliações do vendedor")
    void deveDetalharAnuncioComNotaMediaArredondada() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        DetalharAnuncioResponse esperado = DetalharAnuncioResponse.builder()
                .id(anuncio.getId())
                .nome(anuncio.getNome())
                .vendedor(ResumoVendedorResponse.builder()
                        .id(anuncio.getVendedor().getId())
                        .nome(anuncio.getVendedor().getNome())
                        .notaMedia(4.67)
                        .quantidadeAvaliacoes(3L)
                        .build())
                .build();

        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(avaliacaoRepository.calcularNotaMediaPorVendedorId(anuncio.getVendedor().getId())).thenReturn(4.666666);
        when(avaliacaoRepository.contarPorVendedorId(anuncio.getVendedor().getId())).thenReturn(3L);
        when(detalharAnuncioMapper.toResponse(anuncio, 4.67, 3L)).thenReturn(esperado);

        // Act
        DetalharAnuncioResponse response = service.detalhar(anuncio.getId());

        // Assert
        assertEquals(esperado, response);
        verify(buscarAnuncioService).buscarPorId(anuncio.getId());
        verify(avaliacaoRepository).calcularNotaMediaPorVendedorId(anuncio.getVendedor().getId());
        verify(avaliacaoRepository).contarPorVendedorId(anuncio.getVendedor().getId());
        verify(detalharAnuncioMapper).toResponse(anuncio, 4.67, 3L);
    }

    @Test
    @DisplayName("Deve detalhar anúncio com nota média nula quando vendedor não possuir avaliações")
    void deveDetalharAnuncioSemAvaliacoesComNotaMediaNula() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        DetalharAnuncioResponse esperado = DetalharAnuncioResponse.builder()
                .id(anuncio.getId())
                .vendedor(ResumoVendedorResponse.builder()
                        .id(anuncio.getVendedor().getId())
                        .notaMedia(null)
                        .quantidadeAvaliacoes(0L)
                        .build())
                .build();

        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(avaliacaoRepository.calcularNotaMediaPorVendedorId(anuncio.getVendedor().getId())).thenReturn(null);
        when(avaliacaoRepository.contarPorVendedorId(anuncio.getVendedor().getId())).thenReturn(0L);
        when(detalharAnuncioMapper.toResponse(anuncio, null, 0L)).thenReturn(esperado);

        // Act
        DetalharAnuncioResponse response = service.detalhar(anuncio.getId());

        // Assert
        assertNull(response.vendedor().notaMedia());
        assertEquals(0L, response.vendedor().quantidadeAvaliacoes());
        verify(detalharAnuncioMapper).toResponse(anuncio, null, 0L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando anúncio informado não existir")
    void deveLancarExcecaoQuandoAnuncioNaoExistir() {

        // Arrange
        when(buscarAnuncioService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Anúncio não encontrado"));

        // Act & Assert
        RecursoNaoEncontradoException exception = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.detalhar(99L));

        assertEquals("Anúncio não encontrado", exception.getMessage());
        verifyNoInteractions(avaliacaoRepository, detalharAnuncioMapper);
    }
}
