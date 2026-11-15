package br.com.guildcode.geekhub.service.categoria.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaSemAnunciosValidatorServiceTest {

    @InjectMocks
    private CategoriaSemAnunciosValidatorService validatorService;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Test
    @DisplayName("Deve lançar ConflitoException quando houver anúncios vinculados à categoria")
    void deveLancarExcecaoQuandoCategoriaPossuiAnuncios() {

        // Arrange
        when(anuncioRepository.existsByCategoriaId(1L)).thenReturn(TRUE);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> validatorService.validar(1L)
        );

        assertEquals("Não é possível remover categoria com anúncios vinculados", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando categoria não possuir anúncios vinculados")
    void naoDeveLancarExcecaoQuandoCategoriaSemAnuncios() {

        // Arrange
        when(anuncioRepository.existsByCategoriaId(3L)).thenReturn(FALSE);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar(3L));
    }
}
