package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.service.categoria.validator.CategoriaSemAnunciosValidatorService;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverCategoriaServiceTest {

    @InjectMocks
    private RemoverCategoriaService service;

    @Mock
    private BuscarCategoriaService buscarCategoriaService;

    @Mock
    private CategoriaSemAnunciosValidatorService categoriaSemAnunciosValidatorService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Deve remover categoria quando encontrada e sem anúncios vinculados")
    void deveRemoverCategoriaComSucesso() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        when(buscarCategoriaService.buscarPorId(categoria.getId())).thenReturn(categoria);
        doNothing().when(categoriaSemAnunciosValidatorService).validar(categoria.getId());

        // Act
        service.remover(categoria.getId());

        // Assert
        verify(buscarCategoriaService).buscarPorId(categoria.getId());
        verify(categoriaSemAnunciosValidatorService).validar(categoria.getId());
        verify(categoriaRepository).delete(categoria);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao tentar remover categoria com anúncios vinculados")
    void deveLancarConflitoQuandoCategoriaPossuiAnuncios() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        when(buscarCategoriaService.buscarPorId(categoria.getId())).thenReturn(categoria);
        doThrow(new ConflitoException("Não é possível remover categoria com anúncios vinculados"))
                .when(categoriaSemAnunciosValidatorService).validar(categoria.getId());

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.remover(categoria.getId())
        );

        assertEquals("Não é possível remover categoria com anúncios vinculados", ex.getMessage());
        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar remover categoria inexistente")
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {

        // Arrange
        when(buscarCategoriaService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.remover(99L)
        );

        assertEquals("Categoria não encontrada", ex.getMessage());
        verifyNoInteractions(categoriaSemAnunciosValidatorService);
        verify(categoriaRepository, never()).delete(any());
    }
}
