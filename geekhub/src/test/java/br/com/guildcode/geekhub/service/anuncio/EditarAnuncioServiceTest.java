package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.anuncio.EditarAnuncioRequestFactory;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.anuncio.EditarAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.anuncio.validator.CategoriaAtivaValidatorService;
import br.com.guildcode.geekhub.service.anuncio.validator.ProprietarioAnuncioValidatorService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.categoria.BuscarCategoriaService;
import br.com.guildcode.geekhub.validator.anuncio.EditarAnuncioValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditarAnuncioServiceTest {

    @InjectMocks
    private EditarAnuncioService service;

    @Mock
    private EditarAnuncioValidator editarAnuncioValidator;

    @Mock
    private BuscarAnuncioService buscarAnuncioService;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private ProprietarioAnuncioValidatorService proprietarioAnuncioValidatorService;

    @Mock
    private BuscarCategoriaService buscarCategoriaService;

    @Mock
    private CategoriaAtivaValidatorService categoriaAtivaValidatorService;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private EditarAnuncioMapper editarAnuncioMapper;

    @Test
    @DisplayName("Deve permitir ao proprietário editar anúncio em categoria ativa")
    void deveEditarAnuncioComSucesso() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario proprietario = anuncio.getVendedor();
        Categoria categoria = CategoriaFactory.get();
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.get();

        EditarAnuncioResponse expectedResponse = EditarAnuncioResponse.builder()
                .id(anuncio.getId())
                .nome(request.nome())
                .descricao(request.descricao())
                .categoriaId(categoria.getId())
                .categoriaNome(categoria.getNome())
                .preco(request.preco())
                .quantidadeDisponivel(request.quantidadeDisponivel())
                .disponivel(request.disponivel())
                .condicao(request.condicao())
                .conservacao(request.conservacao())
                .imagens(request.imagens())
                .vendedorId(proprietario.getId())
                .criadoEm(anuncio.getCriadoEm())
                .build();

        doNothing().when(editarAnuncioValidator).validarDadosEdicao(request);
        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(usuarioAutenticadoService.get()).thenReturn(proprietario);
        doNothing().when(proprietarioAnuncioValidatorService).validarProprietario(anuncio, proprietario);
        when(buscarCategoriaService.buscarPorId(request.categoriaId())).thenReturn(categoria);
        doNothing().when(categoriaAtivaValidatorService).validar(categoria);
        when(editarAnuncioMapper.toResponse(anuncio)).thenReturn(expectedResponse);

        // Act
        EditarAnuncioResponse response = service.editar(anuncio.getId(), request);

        // Assert
        assertEquals(request.nome(), anuncio.getNome());
        assertEquals(request.preco(), anuncio.getPreco());
        assertEquals(request.quantidadeDisponivel(), anuncio.getQuantidadeDisponivel());
        assertEquals(request.disponivel(), anuncio.getDisponivel());
        assertEquals(request.imagens(), anuncio.getImagens());
        assertEquals(expectedResponse.nome(), response.nome());

        verify(anuncioRepository).save(anuncio);
        verify(editarAnuncioMapper).toResponse(anuncio);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando outro usuário tentar editar anúncio")
    void deveLancarAccessDeniedQuandoNaoProprietario() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario outroUsuario = UsuarioFactory.get();
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.get();

        doNothing().when(editarAnuncioValidator).validarDadosEdicao(request);
        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(usuarioAutenticadoService.get()).thenReturn(outroUsuario);
        doThrow(new AccessDeniedException("Você não possui permissão para alterar este anúncio"))
                .when(proprietarioAnuncioValidatorService).validarProprietario(anuncio, outroUsuario);

        // Act & Assert
        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.editar(anuncio.getId(), request)
        );

        assertEquals("Você não possui permissão para alterar este anúncio", ex.getMessage());
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando edição apontar para categoria desativada")
    void deveLancarNegocioExceptionQuandoCategoriaInativa() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        Usuario proprietario = anuncio.getVendedor();
        Categoria categoriaInativa = CategoriaFactory.get();
        categoriaInativa.setAtiva(FALSE);
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.get();

        doNothing().when(editarAnuncioValidator).validarDadosEdicao(request);
        when(buscarAnuncioService.buscarPorId(anuncio.getId())).thenReturn(anuncio);
        when(usuarioAutenticadoService.get()).thenReturn(proprietario);
        doNothing().when(proprietarioAnuncioValidatorService).validarProprietario(anuncio, proprietario);
        when(buscarCategoriaService.buscarPorId(request.categoriaId())).thenReturn(categoriaInativa);
        doThrow(new NegocioException("Não é possível vincular anúncio a uma categoria desativada"))
                .when(categoriaAtivaValidatorService).validar(categoriaInativa);

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> service.editar(anuncio.getId(), request)
        );

        assertEquals("Não é possível vincular anúncio a uma categoria desativada", ex.getMessage());
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando anúncio não for encontrado")
    void deveLancarNotFoundQuandoAnuncioInexistente() {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.get();
        doNothing().when(editarAnuncioValidator).validarDadosEdicao(request);
        when(buscarAnuncioService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Anúncio não encontrado"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.editar(99L, request)
        );

        assertEquals("Anúncio não encontrado", ex.getMessage());
        verify(anuncioRepository, never()).save(any());
    }
}
