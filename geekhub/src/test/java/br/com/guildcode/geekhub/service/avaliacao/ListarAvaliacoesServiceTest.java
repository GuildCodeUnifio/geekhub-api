package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.response.AutorAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.ListarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.mapper.avaliacao.ListarAvaliacaoMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarAvaliacoesServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private ListarAvaliacaoMapper listarAvaliacaoMapper;

    @InjectMocks
    private ListarAvaliacoesService service;

    @Test
    @DisplayName("Deve listar todas as avaliações paginadas quando vendedorId não for informado")
    void deveListarTodasAvaliacoesQuandoVendedorIdNulo() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Avaliacao avaliacao = AvaliacaoFactory.get();
        ListarAvaliacaoResponse mappedResponse = ListarAvaliacaoResponse.builder()
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

        when(avaliacaoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(avaliacao), pageable, 1));
        when(listarAvaliacaoMapper.toResponse(avaliacao)).thenReturn(mappedResponse);

        // Act
        Page<ListarAvaliacaoResponse> result = service.listar(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(mappedResponse, result.getContent().getFirst());
        verify(avaliacaoRepository).findAll(pageable);
        verify(avaliacaoRepository, never()).findByVendedorId(any(), any());
    }

    @Test
    @DisplayName("Deve listar avaliações filtradas por vendedorId quando parâmetro for informado")
    void deveListarAvaliacoesFiltradasPorVendedor() {
        // Arrange
        Long vendedorId = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        Avaliacao avaliacao = AvaliacaoFactory.get();
        ListarAvaliacaoResponse mappedResponse = ListarAvaliacaoResponse.builder()
                .id(avaliacao.getId())
                .pedidoId(avaliacao.getPedido().getId())
                .vendedorId(vendedorId)
                .autor(AutorAvaliacaoResponse.builder()
                        .id(avaliacao.getAutor().getId())
                        .nome(avaliacao.getAutor().getNome())
                        .imagemPerfil(avaliacao.getAutor().getImagemPerfil())
                        .build())
                .nota(avaliacao.getNota())
                .comentario(avaliacao.getComentario())
                .criadoEm(avaliacao.getCriadoEm())
                .build();

        when(avaliacaoRepository.findByVendedorId(vendedorId, pageable))
                .thenReturn(new PageImpl<>(List.of(avaliacao), pageable, 1));
        when(listarAvaliacaoMapper.toResponse(avaliacao)).thenReturn(mappedResponse);

        // Act
        Page<ListarAvaliacaoResponse> result = service.listar(vendedorId, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(mappedResponse, result.getContent().getFirst());
        verify(avaliacaoRepository).findByVendedorId(vendedorId, pageable);
        verify(avaliacaoRepository, never()).findAll(any(Pageable.class));
    }
}
