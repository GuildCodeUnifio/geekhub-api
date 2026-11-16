package br.com.guildcode.geekhub.domain.avaliacao;

import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvaliacaoTest {

    @Test
    @DisplayName("Deve identificar corretamente se usuário informado é autor da avaliação")
    void deveIdentificarAutorDaAvaliacao() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();

        // Act & Assert
        assertTrue(avaliacao.pertenceAoAutor(avaliacao.getAutor().getId()));
        assertFalse(avaliacao.pertenceAoAutor(999L));
    }
}
