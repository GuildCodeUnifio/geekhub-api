package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.CadastrarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.factory.categoria.CadastrarCategoriaRequestFactory;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.mapper.categoria.CadastrarCategoriaMapper;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.service.categoria.validator.CategoriaUnicaValidatorService;
import br.com.guildcode.geekhub.validator.categoria.CadastrarCategoriaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarCategoriaServiceTest {

    @InjectMocks
    private CadastrarCategoriaService service;

    @Mock
    private CadastrarCategoriaValidator cadastrarCategoriaValidator;

    @Mock
    private CategoriaUnicaValidatorService categoriaUnicaValidatorService;

    @Mock
    private CadastrarCategoriaMapper cadastrarCategoriaMapper;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Test
    @DisplayName("Deve cadastrar categoria ativa por padrão e salvar no repositório")
    void deveCadastrarCategoriaComSucesso() {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequestFactory.get();
        Categoria categoria = CategoriaFactory.get();
        categoria.setAtiva(null);
        categoria.setCriadoEm(null);

        CadastrarCategoriaResponse expectedResponse = CadastrarCategoriaResponse.builder()
                .id(categoria.getId())
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativa(TRUE)
                .criadoEm(CategoriaFactory.get().getCriadoEm())
                .build();

        doNothing().when(cadastrarCategoriaValidator).validarDadosCadastro(request);
        doNothing().when(categoriaUnicaValidatorService).validar(request.nome());
        when(cadastrarCategoriaMapper.toEntity(request)).thenReturn(categoria);
        when(cadastrarCategoriaMapper.toResponse(categoria)).thenReturn(expectedResponse);

        // Act
        CadastrarCategoriaResponse response = service.cadastrar(request);

        // Assert
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.nome(), response.nome());
        assertEquals(TRUE, categoria.getAtiva());
        assertNotNull(categoria.getCriadoEm());

        verify(cadastrarCategoriaValidator).validarDadosCadastro(request);
        verify(categoriaUnicaValidatorService).validar(request.nome());
        verify(cadastrarCategoriaMapper).toEntity(request);
        verify(categoriaRepository).save(categoria);
        verify(cadastrarCategoriaMapper).toResponse(categoria);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException ao tentar cadastrar categoria com nome duplicado")
    void deveLancarConflitoQuandoNomeDuplicado() {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequestFactory.get();

        doNothing().when(cadastrarCategoriaValidator).validarDadosCadastro(request);
        doThrow(new ConflitoException("Categoria já cadastrada"))
                .when(categoriaUnicaValidatorService).validar(request.nome());

        // Act & Assert
        ConflitoException ex = assertThrows(
                ConflitoException.class,
                () -> service.cadastrar(request)
        );

        assertEquals("Categoria já cadastrada", ex.getMessage());
        verify(categoriaRepository, never()).save(any());
    }
}
