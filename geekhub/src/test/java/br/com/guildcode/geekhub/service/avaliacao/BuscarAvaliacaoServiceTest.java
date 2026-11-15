package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
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
class BuscarAvaliacaoServiceTest {

    @InjectMocks
    private BuscarAvaliacaoService service;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Test
    @DisplayName("Deve retornar avaliação quando ID encontrado no repositório")
    void deveBuscarAvaliacaoPorId() {

        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();
        when(avaliacaoRepository.findById(avaliacao.getId())).thenReturn(of(avaliacao));

        // Act
        Avaliacao resultado = service.buscarPorId(avaliacao.getId());

        // Assert
        assertEquals(avaliacao.getId(), resultado.getId());
        assertEquals(avaliacao.getNota(), resultado.getNota());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando avaliação não encontrada por ID")
    void deveLancarExcecaoQuandoIdNaoEncontrado() {

        // Arrange
        when(avaliacaoRepository.findById(99L)).thenReturn(empty());

        // Act
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L)
        );

        // Assert
        assertEquals("Avaliação não encontrada", ex.getMessage());
    }
}
