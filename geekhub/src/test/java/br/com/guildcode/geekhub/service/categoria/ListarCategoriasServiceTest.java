package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.ListarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.mapper.categoria.ListarCategoriaMapper;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Page.empty;
import static org.springframework.data.domain.PageRequest.of;

@ExtendWith(MockitoExtension.class)
class ListarCategoriasServiceTest {

    @InjectMocks
    private ListarCategoriasService service;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ListarCategoriaMapper listarCategoriaMapper;

    @Test
    @DisplayName("Deve listar todas as categorias paginadas quando filtro ativa for nulo")
    void deveListarTodasCategoriasQuandoFiltroNulo() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        Pageable pageable = of(0, 10);
        Page<Categoria> pagina = new PageImpl<>(List.of(categoria));
        ListarCategoriaResponse expectedResponse = ListarCategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativa(categoria.getAtiva())
                .criadoEm(categoria.getCriadoEm())
                .build();

        when(categoriaRepository.findAll(pageable)).thenReturn(pagina);
        when(listarCategoriaMapper.toResponse(categoria)).thenReturn(expectedResponse);

        // Act
        Page<ListarCategoriaResponse> resultado = service.listar(null, pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(expectedResponse.id(), resultado.getContent().get(0).id());
        assertEquals(expectedResponse.nome(), resultado.getContent().get(0).nome());

        verify(categoriaRepository).findAll(pageable);
        verify(categoriaRepository, never()).findByAtiva(any(), any());
        verify(listarCategoriaMapper).toResponse(categoria);
    }

    @Test
    @DisplayName("Deve filtrar categorias pelo parâmetro ativa quando informado")
    void deveFiltrarCategoriasPorStatusAtiva() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        Pageable pageable = of(0, 10);
        Page<Categoria> pagina = new PageImpl<>(List.of(categoria));
        ListarCategoriaResponse expectedResponse = ListarCategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativa(TRUE)
                .criadoEm(categoria.getCriadoEm())
                .build();

        when(categoriaRepository.findByAtiva(TRUE, pageable)).thenReturn(pagina);
        when(listarCategoriaMapper.toResponse(categoria)).thenReturn(expectedResponse);

        // Act
        Page<ListarCategoriaResponse> resultado = service.listar(TRUE, pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(TRUE, resultado.getContent().get(0).ativa());

        verify(categoriaRepository).findByAtiva(TRUE, pageable);
        verify(categoriaRepository, never()).findAll(any(Pageable.class));
        verify(listarCategoriaMapper).toResponse(categoria);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não houver categorias para o filtro informado")
    void deveRetornarPaginaVaziaQuandoNaoHouverCategoriasInativas() {

        // Arrange
        Pageable pageable = of(0, 10);
        Page<Categoria> paginaVazia = empty();

        when(categoriaRepository.findByAtiva(FALSE, pageable)).thenReturn(paginaVazia);

        // Act
        Page<ListarCategoriaResponse> resultado = service.listar(FALSE, pageable);

        // Assert
        assertEquals(0, resultado.getTotalElements());
        verify(categoriaRepository).findByAtiva(FALSE, pageable);
    }
}
