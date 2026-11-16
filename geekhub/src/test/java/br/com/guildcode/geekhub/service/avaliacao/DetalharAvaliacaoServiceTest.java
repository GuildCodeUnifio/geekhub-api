package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.response.AutorAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.DetalharAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.mapper.avaliacao.DetalharAvaliacaoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalharAvaliacaoServiceTest {

    @Mock
    private BuscarAvaliacaoService buscarAvaliacaoService;

    @Mock
    private DetalharAvaliacaoMapper detalharAvaliacaoMapper;

    @InjectMocks
    private DetalharAvaliacaoService service;

    @Test
    @DisplayName("Deve detalhar avaliação por id com sucesso")
    void deveDetalharAvaliacaoComSucesso() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        DetalharAvaliacaoResponse expectedResponse = DetalharAvaliacaoResponse.builder()
                .id(avaliacao.getId())
                .pedidoId(avaliacao.getPedido().getId())
                .vendedorId(avaliacao.getVendedor().getId())
                .autor(AutorAvaliacaoResponse.builder()
                        .id(avaliacao.getAutor().getId())
                        .nome(avaliacao.getAutor().getNome())
                        .imagemPerfil(avaliacao.getAutor().getImagemPerfil())
                        .build())
                .nota(avaliacao.getNota())
                .comentario(avaliacao.getComentario())
                .criadoEm(avaliacao.getCriadoEm())
                .build();

        when(buscarAvaliacaoService.buscarPorId(avaliacao.getId())).thenReturn(avaliacao);
        when(detalharAvaliacaoMapper.toResponse(avaliacao)).thenReturn(expectedResponse);

        // Act
        DetalharAvaliacaoResponse response = service.detalhar(avaliacao.getId());

        // Assert
        assertEquals(expectedResponse, response);
        verify(buscarAvaliacaoService).buscarPorId(avaliacao.getId());
        verify(detalharAvaliacaoMapper).toResponse(avaliacao);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar detalhar avaliação inexistente")
    void deveLancarExcecaoQuandoAvaliacaoNaoEncontrada() {
        // Arrange
        when(buscarAvaliacaoService.buscarPorId(999L))
                .thenThrow(new RecursoNaoEncontradoException("Avaliação não encontrada"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.detalhar(999L)
        );

        assertEquals("Avaliação não encontrada", ex.getMessage());
        verifyNoInteractions(detalharAvaliacaoMapper);
    }
}
