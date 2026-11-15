package br.com.guildcode.geekhub.service.categoria.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
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
class CategoriaUnicaValidatorServiceTest {

    @InjectMocks
    private CategoriaUnicaValidatorService validatorService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Deve lançar ConflitoException quando já existir categoria cadastrada com o mesmo nome")
    void deveLancarExcecaoQuandoNomeJaCadastrado() {

        // Arrange
        String nome = CategoriaFactory.get().getNome();
        when(categoriaRepository.existsByNomeIgnoreCase(nome)).thenReturn(TRUE);

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> validatorService.validar(nome)
        );

        assertEquals("Categoria já cadastrada", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve lançar exceção quando o nome da categoria ainda estiver disponível")
    void naoDeveLancarExcecaoQuandoNomeDisponivel() {

        // Arrange
        when(categoriaRepository.existsByNomeIgnoreCase("Board Games")).thenReturn(FALSE);

        // Act & Assert
        assertDoesNotThrow(() -> validatorService.validar("Board Games"));
    }
}
