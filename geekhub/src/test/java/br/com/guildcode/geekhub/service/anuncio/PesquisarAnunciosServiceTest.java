package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.mapper.anuncio.PesquisarAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.validator.anuncio.PesquisarAnunciosValidator;
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

import java.math.BigDecimal;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PesquisarAnunciosServiceTest {

    @InjectMocks
    private PesquisarAnunciosService service;

    @Mock
    private PesquisarAnunciosValidator pesquisarAnunciosValidator;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private PesquisarAnuncioMapper pesquisarAnuncioMapper;

    @Test
    @DisplayName("Deve normalizar nome com trim e aplicar todos os filtros combinados no catálogo")
    void deveNormalizarNomeECombinarFiltros() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        ListarAnuncioResponse item = ListarAnuncioResponse.builder()
                .id(anuncio.getId())
                .nome(anuncio.getNome())
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Anuncio> paginaAnuncios = new PageImpl<>(List.of(anuncio), pageable, 1);

        BigDecimal precoMin = BigDecimal.valueOf(100);
        BigDecimal precoMax = BigDecimal.valueOf(500);

        doNothing().when(pesquisarAnunciosValidator).validarFaixaPreco(precoMin, precoMax);
        when(anuncioRepository.pesquisarCatalogo("Naruto", 1L, precoMin, precoMax, NOVO, EXCELENTE, 3L, pageable))
                .thenReturn(paginaAnuncios);
        when(pesquisarAnuncioMapper.toResponse(anuncio)).thenReturn(item);

        // Act
        Page<ListarAnuncioResponse> pagina = service.pesquisar(
                "  Naruto  ", 1L, precoMin, precoMax, NOVO, EXCELENTE, 3L, pageable);

        // Assert
        assertEquals(1, pagina.getTotalElements());
        assertEquals(item, pagina.getContent().getFirst());
        verify(pesquisarAnunciosValidator).validarFaixaPreco(precoMin, precoMax);
        verify(anuncioRepository).pesquisarCatalogo("Naruto", 1L, precoMin, precoMax, NOVO, EXCELENTE, 3L, pageable);
    }

    @Test
    @DisplayName("Deve converter nome em branco para filtro nulo na pesquisa do catálogo")
    void deveConverterNomeEmBrancoParaNulo() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(anuncioRepository.pesquisarCatalogo(null, null, null, null, null, null, null, pageable))
                .thenReturn(Page.empty(pageable));

        // Act
        Page<ListarAnuncioResponse> pagina = service.pesquisar("   ", null, null, null, null, null, null, pageable);

        // Assert
        assertEquals(0, pagina.getTotalElements());
        verify(anuncioRepository).pesquisarCatalogo(null, null, null, null, null, null, null, pageable);
    }

    @Test
    @DisplayName("Deve aceitar nome nulo mantendo filtro ausente na pesquisa")
    void deveManterNomeNuloQuandoNaoInformado() {

        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(anuncioRepository.pesquisarCatalogo(null, null, null, null, null, null, null, pageable))
                .thenReturn(Page.empty(pageable));

        // Act
        Page<ListarAnuncioResponse> pagina = service.pesquisar(null, null, null, null, null, null, null, pageable);

        // Assert
        assertEquals(0, pagina.getTotalElements());
        verify(anuncioRepository).pesquisarCatalogo(null, null, null, null, null, null, null, pageable);
    }

    @Test
    @DisplayName("Deve propagar NegocioException sem consultar o repositório quando faixa de preço for inválida")
    void devePropagarExcecaoQuandoFaixaDePrecoInvalida() {

        // Arrange
        BigDecimal precoMin = BigDecimal.valueOf(500);
        BigDecimal precoMax = BigDecimal.valueOf(100);
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new NegocioException("Preço mínimo não pode ser maior que o preço máximo"))
                .when(pesquisarAnunciosValidator).validarFaixaPreco(precoMin, precoMax);

        // Act & Assert
        NegocioException exception = assertThrows(
                NegocioException.class,
                () -> service.pesquisar(null, null, precoMin, precoMax, null, null, null, pageable));

        assertEquals("Preço mínimo não pode ser maior que o preço máximo", exception.getMessage());
        verifyNoInteractions(anuncioRepository, pesquisarAnuncioMapper);
    }
}
