package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.DetalharCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.mapper.categoria.DetalharCategoriaMapper;
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
class DetalharCategoriaServiceTest {

    @InjectMocks
    private DetalharCategoriaService service;

    @Mock
    private BuscarCategoriaService buscarCategoriaService;

    @Mock
    private DetalharCategoriaMapper detalharCategoriaMapper;

    @Test
    @DisplayName("Deve retornar detalhes da categoria encontrada por ID")
    void deveDetalharCategoriaComSucesso() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        DetalharCategoriaResponse expectedResponse = DetalharCategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativa(categoria.getAtiva())
                .criadoEm(categoria.getCriadoEm())
                .build();

        when(buscarCategoriaService.buscarPorId(categoria.getId())).thenReturn(categoria);
        when(detalharCategoriaMapper.toResponse(categoria)).thenReturn(expectedResponse);

        // Act
        DetalharCategoriaResponse response = service.detalhar(categoria.getId());

        // Assert
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.nome(), response.nome());
        assertEquals(expectedResponse.ativa(), response.ativa());

        verify(buscarCategoriaService).buscarPorId(categoria.getId());
        verify(detalharCategoriaMapper).toResponse(categoria);
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando categoria não for encontrada")
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {

        // Arrange
        when(buscarCategoriaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.detalhar(99L)
        );

        assertEquals("Categoria não encontrada", ex.getMessage());
        verifyNoInteractions(detalharCategoriaMapper);
    }
}
