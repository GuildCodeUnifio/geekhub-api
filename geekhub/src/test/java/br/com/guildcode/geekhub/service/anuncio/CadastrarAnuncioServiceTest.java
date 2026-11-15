package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.anuncio.CadastrarAnuncioRequestFactory;
import br.com.guildcode.geekhub.factory.categoria.CategoriaFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.anuncio.CadastrarAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.anuncio.validator.CategoriaAtivaValidatorService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.categoria.BuscarCategoriaService;
import br.com.guildcode.geekhub.validator.anuncio.CadastrarAnuncioValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.Boolean.FALSE;
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
class CadastrarAnuncioServiceTest {

    @InjectMocks
    private CadastrarAnuncioService service;

    @Mock
    private CadastrarAnuncioValidator cadastrarAnuncioValidator;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarCategoriaService buscarCategoriaService;

    @Mock
    private CategoriaAtivaValidatorService categoriaAtivaValidatorService;

    @Mock
    private CadastrarAnuncioMapper cadastrarAnuncioMapper;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Test
    @DisplayName("Deve cadastrar anúncio vinculado ao vendedor autenticado e à categoria ativa")
    void deveCadastrarAnuncioComSucesso() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();
        Usuario vendedor = UsuarioFactory.get();
        Categoria categoria = CategoriaFactory.get();
        Anuncio anuncio = AnuncioFactory.get();
        anuncio.setDisponivel(null);
        anuncio.setCriadoEm(null);

        CadastrarAnuncioResponse expectedResponse = CadastrarAnuncioResponse.builder()
                .id(anuncio.getId())
                .nome(request.nome())
                .descricao(request.descricao())
                .categoriaId(categoria.getId())
                .categoriaNome(categoria.getNome())
                .preco(request.preco())
                .quantidadeDisponivel(request.quantidadeDisponivel())
                .disponivel(TRUE)
                .condicao(request.condicao())
                .conservacao(request.conservacao())
                .imagens(request.imagens())
                .vendedorId(vendedor.getId())
                .criadoEm(AnuncioFactory.get().getCriadoEm())
                .build();

        doNothing().when(cadastrarAnuncioValidator).validarDadosCadastro(request);
        when(usuarioAutenticadoService.get()).thenReturn(vendedor);
        when(buscarCategoriaService.buscarPorId(request.categoriaId())).thenReturn(categoria);
        doNothing().when(categoriaAtivaValidatorService).validar(categoria);
        when(cadastrarAnuncioMapper.toEntity(request)).thenReturn(anuncio);
        when(cadastrarAnuncioMapper.toResponse(anuncio)).thenReturn(expectedResponse);

        // Act
        CadastrarAnuncioResponse response = service.cadastrar(request);

        // Assert
        assertEquals(expectedResponse.id(), response.id());
        assertEquals(expectedResponse.nome(), response.nome());
        assertEquals(vendedor, anuncio.getVendedor());
        assertEquals(categoria, anuncio.getCategoria());
        assertEquals(TRUE, anuncio.getDisponivel());
        assertNotNull(anuncio.getCriadoEm());

        verify(cadastrarAnuncioValidator).validarDadosCadastro(request);
        verify(usuarioAutenticadoService).get();
        verify(buscarCategoriaService).buscarPorId(request.categoriaId());
        verify(categoriaAtivaValidatorService).validar(categoria);
        verify(anuncioRepository).save(anuncio);
        verify(cadastrarAnuncioMapper).toResponse(anuncio);
    }

    @Test
    @DisplayName("Deve lançar NegocioException ao tentar cadastrar anúncio em categoria desativada")
    void deveLancarNegocioExceptionQuandoCategoriaInativa() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();
        Usuario vendedor = UsuarioFactory.get();
        Categoria categoriaInativa = CategoriaFactory.get();
        categoriaInativa.setAtiva(FALSE);

        doNothing().when(cadastrarAnuncioValidator).validarDadosCadastro(request);
        when(usuarioAutenticadoService.get()).thenReturn(vendedor);
        when(buscarCategoriaService.buscarPorId(request.categoriaId())).thenReturn(categoriaInativa);
        doThrow(new NegocioException("Não é possível vincular anúncio a uma categoria desativada"))
                .when(categoriaAtivaValidatorService).validar(categoriaInativa);

        // Act & Assert
        NegocioException ex = assertThrows(
                NegocioException.class,
                () -> service.cadastrar(request)
        );

        assertEquals("Não é possível vincular anúncio a uma categoria desativada", ex.getMessage());
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException ao tentar cadastrar anúncio em categoria inexistente")
    void deveLancarNotFoundQuandoCategoriaInexistente() {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();
        Usuario vendedor = UsuarioFactory.get();

        doNothing().when(cadastrarAnuncioValidator).validarDadosCadastro(request);
        when(usuarioAutenticadoService.get()).thenReturn(vendedor);
        when(buscarCategoriaService.buscarPorId(request.categoriaId()))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        // Act & Assert
        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.cadastrar(request)
        );

        assertEquals("Categoria não encontrada", ex.getMessage());
        verify(anuncioRepository, never()).save(any());
    }
}
