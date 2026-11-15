package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarCategoriaServiceTest {

    @InjectMocks
    private BuscarCategoriaService service;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Deve retornar categoria quando ID encontrado no repositório")
    void deveBuscarCategoriaPorId() {

        // Arrange
        Categoria categoria = CategoriaFactory.get();
        when(categoriaRepository.findById(categoria.getId())).thenReturn(of(categoria));

        // Act
        Categoria resultado = service.buscarPorId(categoria.getId());

        // Assert
        assertEquals(categoria.getId(), resultado.getId());
        assertEquals(categoria.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando categoria não encontrada por ID")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {

        // Arrange
        when(categoriaRepository.findById(99L)).thenReturn(empty());

        // Act
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        // Assert
        assertEquals("Categoria não encontrada", ex.getMessage());
    }
}
