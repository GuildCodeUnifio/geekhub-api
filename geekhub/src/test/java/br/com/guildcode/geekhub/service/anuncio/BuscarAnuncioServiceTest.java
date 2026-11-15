package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
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
class BuscarAnuncioServiceTest {

    @InjectMocks
    private BuscarAnuncioService service;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Test
    @DisplayName("Deve retornar anúncio quando ID encontrado no repositório")
    void deveBuscarAnuncioPorId() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        when(anuncioRepository.findById(anuncio.getId())).thenReturn(of(anuncio));

        // Act
        Anuncio resultado = service.buscarPorId(anuncio.getId());

        // Assert
        assertEquals(anuncio.getId(), resultado.getId());
        assertEquals(anuncio.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando anúncio não encontrado por ID")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {

        // Arrange
        when(anuncioRepository.findById(99L)).thenReturn(empty());

        // Act
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        // Assert
        assertEquals("Anúncio não encontrado", ex.getMessage());
    }
}
