package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.EditarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.factory.categoria.EditarCategoriaRequestFactory;
import br.com.guildcode.geekhub.mapper.categoria.EditarCategoriaMapper;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.service.categoria.validator.CategoriaUnicaValidatorService;
import br.com.guildcode.geekhub.validator.categoria.EditarCategoriaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditarCategoriaServiceTest {

    @InjectMocks
    private EditarCategoriaService service;

    @Mock
    private EditarCategoriaValidator editarCategoriaValidator;

    @Mock
    private BuscarCategoriaService buscarCategoriaService;

    @Mock
    private CategoriaUnicaValidatorService categoriaUnicaValidatorService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private EditarCategoriaMapper editarCategoriaMapper;

    @Test
    @DisplayName("Deve editar categoria mantendo o mesmo nome sem validar unicidade")
    void deveEditarCategoriaMesmoNome() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        EditarCategoriaRequest request = EditarCategoriaRequestFactory.get();
        EditarCategoriaResponse expectedResponse = EditarCategoriaResponse.builder()
                .id(categoria.getId())
                .nome(request.nome())
                .descricao(request.descricao())
                .ativa(request.ativa())
                .criadoEm(categoria.getCriadoEm())
                .build();

        doNothing().when(editarCategoriaValidator).validarDadosEdicao(request);
        when(buscarCategoriaService.buscarPorId(categoria.getId())).thenReturn(categoria);
        when(editarCategoriaMapper.toResponse(categoria)).thenReturn(expectedResponse);

        // Act
        EditarCategoriaResponse response = service.editar(categoria.getId(), request);

        // Assert
        assertEquals(request.nome(), categoria.getNome());
        assertEquals(request.descricao(), categoria.getDescricao());
        assertEquals(request.ativa(), categoria.getAtiva());
        assertEquals(expectedResponse.nome(), response.nome());

        verify(editarCategoriaValidator).validarDadosEdicao(request);
        verify(categoriaUnicaValidatorService, never()).validar(request.nome());
        verify(categoriaRepository).save(categoria);
        verify(editarCategoriaMapper).toResponse(categoria);
    }

    @Test
    @DisplayName("Deve validar unicidade do novo nome ao editar categoria com nome diferente e alterar status ativa")
    void deveEditarCategoriaComNovoNomeValido() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        EditarCategoriaRequest request = EditarCategoriaRequestFactory.comNovoNome();
        EditarCategoriaResponse expectedResponse = EditarCategoriaResponse.builder()
                .id(categoria.getId())
                .nome(request.nome())
                .descricao(request.descricao())
                .ativa(request.ativa())
                .criadoEm(categoria.getCriadoEm())
                .build();

        doNothing().when(editarCategoriaValidator).validarDadosEdicao(request);
        when(buscarCategoriaService.buscarPorId(categoria.getId())).thenReturn(categoria);
        doNothing().when(categoriaUnicaValidatorService).validar(request.nome());
        when(editarCategoriaMapper.toResponse(categoria)).thenReturn(expectedResponse);

        // Act
        EditarCategoriaResponse response = service.editar(categoria.getId(), request);

        // Assert
        assertEquals(request.nome(), categoria.getNome());
        assertEquals(request.ativa(), categoria.getAtiva());
        assertEquals(expectedResponse.nome(), response.nome());
        assertEquals(expectedResponse.ativa(), response.ativa());

        verify(editarCategoriaValidator).validarDadosEdicao(request);
        verify(categoriaUnicaValidatorService).validar(request.nome());
        verify(categoriaRepository).save(categoria);
        verify(editarCategoriaMapper).toResponse(categoria);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao editar categoria para um nome já existente")
    void deveLancarConflitoQuandoNovoNomeDuplicado() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        EditarCategoriaRequest request = EditarCategoriaRequestFactory.comNovoNome();

        doNothing().when(editarCategoriaValidator).validarDadosEdicao(request);
        when(buscarCategoriaService.buscarPorId(categoria.getId())).thenReturn(categoria);
        doThrow(new ConflitoException("Categoria já cadastrada"))
                .when(categoriaUnicaValidatorService).validar(request.nome());

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.editar(categoria.getId(), request)
        );

        assertEquals("Categoria já cadastrada", ex.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando categoria a editar não for encontrada")
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {

        // Arrange
        EditarCategoriaRequest request = EditarCategoriaRequestFactory.get();
        doNothing().when(editarCategoriaValidator).validarDadosEdicao(request);
        when(buscarCategoriaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.editar(99L, request)
        );

        assertEquals("Categoria não encontrada", ex.getMessage());
        verify(categoriaRepository, never()).save(any());
    }
}
