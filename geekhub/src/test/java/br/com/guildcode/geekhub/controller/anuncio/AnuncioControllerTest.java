package br.com.guildcode.geekhub.controller.anuncio;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.DetalharAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioAdminResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarMeuAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ResumoVendedorResponse;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.CadastrarAnuncioRequestFactory;
import br.com.guildcode.geekhub.factory.anuncio.EditarAnuncioRequestFactory;
import br.com.guildcode.geekhub.service.anuncio.CadastrarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.DetalharAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.EditarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.ListarAnunciosAdminService;
import br.com.guildcode.geekhub.service.anuncio.ListarMeusAnunciosService;
import br.com.guildcode.geekhub.service.anuncio.PesquisarAnunciosService;
import br.com.guildcode.geekhub.service.anuncio.RemoverAnuncioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.USADO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.BOM;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnuncioController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class AnuncioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CadastrarAnuncioService cadastrarAnuncioService;

    @MockitoBean
    private PesquisarAnunciosService pesquisarAnunciosService;

    @MockitoBean
    private DetalharAnuncioService detalharAnuncioService;

    @MockitoBean
    private ListarMeusAnunciosService listarMeusAnunciosService;

    @MockitoBean
    private ListarAnunciosAdminService listarAnunciosAdminService;

    @MockitoBean
    private EditarAnuncioService editarAnuncioService;

    @MockitoBean
    private RemoverAnuncioService removerAnuncioService;

    @Test
    @DisplayName("Deve retornar 401 ao tentar cadastrar anúncio sem autenticação")
    void deveRetornar401AoCadastrarSemAutenticacao() throws Exception {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();

        // Act & Assert
        mockMvc.perform(post("/anuncios")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cadastrarAnuncioService);
    }

    @Test
    @DisplayName("Deve cadastrar anúncio válido para usuário autenticado e retornar 201 Created")
    void deveCadastrarAnuncioComSucesso() throws Exception {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();
        CadastrarAnuncioResponse response = CadastrarAnuncioResponse.builder()
                .id(6L)
                .nome(request.nome())
                .descricao(request.descricao())
                .categoriaId(1L)
                .categoriaNome("Figuras colecionáveis")
                .preco(request.preco())
                .quantidadeDisponivel(request.quantidadeDisponivel())
                .disponivel(TRUE)
                .condicao(request.condicao())
                .conservacao(request.conservacao())
                .imagens(request.imagens())
                .vendedorId(3L)
                .criadoEm(LocalDateTime.of(2026, 11, 15, 17, 50))
                .build();

        when(cadastrarAnuncioService.cadastrar(any(CadastrarAnuncioRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/anuncios")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.nome").value(request.nome()))
                .andExpect(jsonPath("$.disponivel").value(true))
                .andExpect(jsonPath("$.vendedorId").value(3L));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar cadastrar anúncio com preço inválido ou sem imagens")
    void deveRetornar400AoCadastrarComDadosInvalidos() throws Exception {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequest.builder()
                .nome("Action figure")
                .descricao("Descrição")
                .categoriaId(1L)
                .preco(BigDecimal.ZERO)
                .quantidadeDisponivel(1)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of())
                .build();

        // Act & Assert
        mockMvc.perform(post("/anuncios")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(cadastrarAnuncioService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar cadastrar anúncio em categoria desativada")
    void deveRetornar400QuandoCategoriaDesativada() throws Exception {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();
        when(cadastrarAnuncioService.cadastrar(any(CadastrarAnuncioRequest.class)))
                .thenThrow(new NegocioException("Não é possível vincular anúncio a uma categoria desativada"));

        // Act & Assert
        mockMvc.perform(post("/anuncios")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Não é possível vincular anúncio a uma categoria desativada"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar cadastrar anúncio com categoria inexistente")
    void deveRetornar404QuandoCategoriaInexistente() throws Exception {

        // Arrange
        CadastrarAnuncioRequest request = CadastrarAnuncioRequestFactory.get();
        when(cadastrarAnuncioService.cadastrar(any(CadastrarAnuncioRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        // Act & Assert
        mockMvc.perform(post("/anuncios")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve editar anúncio próprio e retornar 200 OK")
    void deveEditarAnuncioComSucesso() throws Exception {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.get();
        EditarAnuncioResponse response = EditarAnuncioResponse.builder()
                .id(1L)
                .nome(request.nome())
                .descricao(request.descricao())
                .categoriaId(1L)
                .categoriaNome("Figuras colecionáveis")
                .preco(request.preco())
                .quantidadeDisponivel(request.quantidadeDisponivel())
                .disponivel(request.disponivel())
                .condicao(request.condicao())
                .conservacao(request.conservacao())
                .imagens(request.imagens())
                .vendedorId(3L)
                .criadoEm(LocalDateTime.of(2026, 11, 13, 18, 10))
                .build();

        when(editarAnuncioService.editar(eq(1L), any(EditarAnuncioRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/anuncios/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value(request.nome()));
    }

    @Test
    @DisplayName("Deve retornar 403 quando outro usuário tentar editar anúncio")
    void deveRetornar403QuandoOutroUsuarioEditarAnuncio() throws Exception {

        // Arrange
        EditarAnuncioRequest request = EditarAnuncioRequestFactory.get();
        when(editarAnuncioService.editar(eq(1L), any(EditarAnuncioRequest.class)))
                .thenThrow(new AccessDeniedException("Você não possui permissão para alterar este anúncio"));

        // Act & Assert
        mockMvc.perform(put("/anuncios/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Deve remover anúncio e retornar 204 No Content")
    void deveRemoverAnuncioComSucesso() throws Exception {

        // Arrange
        doNothing().when(removerAnuncioService).remover(4L);

        // Act & Assert
        mockMvc.perform(delete("/anuncios/4")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(removerAnuncioService).remover(4L);
    }

    @Test
    @DisplayName("Deve retornar 403 quando terceiro tentar remover anúncio")
    void deveRetornar403AoRemoverAnuncioDeOutroUsuario() throws Exception {

        // Arrange
        doThrow(new AccessDeniedException("Você não possui permissão para remover este anúncio"))
                .when(removerAnuncioService).remover(1L);

        // Act & Assert
        mockMvc.perform(delete("/anuncios/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar remover anúncio vinculado a pedidos")
    void deveRetornar409AoRemoverAnuncioComPedidos() throws Exception {

        // Arrange
        doThrow(new ConflitoException("Não é possível remover anúncio pois existem pedidos vinculados"))
                .when(removerAnuncioService).remover(1L);

        // Act & Assert
        mockMvc.perform(delete("/anuncios/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Não é possível remover anúncio pois existem pedidos vinculados"));
    }

    @Test
    @DisplayName("Deve detalhar anúncio publicamente sem autenticação retornando dados públicos do vendedor")
    void deveDetalharAnuncioPublicamenteSemAutenticacao() throws Exception {

        // Arrange
        DetalharAnuncioResponse response = DetalharAnuncioResponse.builder()
                .id(1L)
                .nome("Action Figure Goku SSJ")
                .descricao("Figura colecionável edição especial")
                .categoriaId(1L)
                .categoriaNome("Figuras colecionáveis")
                .preco(BigDecimal.valueOf(299.90))
                .quantidadeDisponivel(2)
                .disponivel(TRUE)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .imagens(List.of("https://picsum.photos/200"))
                .vendedor(ResumoVendedorResponse.builder()
                        .id(3L)
                        .nome("Vendedor GeekHub")
                        .imagemPerfil("https://picsum.photos/200")
                        .notaMedia(5.0)
                        .quantidadeAvaliacoes(1L)
                        .build())
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        when(detalharAnuncioService.detalhar(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/anuncios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Action Figure Goku SSJ"))
                .andExpect(jsonPath("$.vendedor.id").value(3L))
                .andExpect(jsonPath("$.vendedor.nome").value("Vendedor GeekHub"))
                .andExpect(jsonPath("$.vendedor.notaMedia").value(5.0))
                .andExpect(jsonPath("$.vendedor.quantidadeAvaliacoes").value(1L))
                .andExpect(jsonPath("$.vendedor.email").doesNotExist())
                .andExpect(jsonPath("$.vendedor.senha").doesNotExist());

        verify(detalharAnuncioService).detalhar(1L);
    }

    @Test
    @DisplayName("Deve retornar 404 ao consultar detalhes de anúncio inexistente")
    void deveRetornar404AoDetalharAnuncioInexistente() throws Exception {

        // Arrange
        when(detalharAnuncioService.detalhar(99L))
                .thenThrow(new RecursoNaoEncontradoException("Anúncio não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/anuncios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Anúncio não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar listar meus anúncios sem autenticação")
    void deveRetornar401AoListarMeusAnunciosSemAutenticacao() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/anuncios/meus"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(listarMeusAnunciosService);
    }

    @Test
    @DisplayName("Deve listar meus anúncios para usuário autenticado incluindo inativos")
    void deveListarMeusAnunciosParaUsuarioAutenticado() throws Exception {

        // Arrange
        ListarMeuAnuncioResponse item = ListarMeuAnuncioResponse.builder()
                .id(5L)
                .nome("HQ Batman Ano Um")
                .preco(BigDecimal.valueOf(85.00))
                .quantidadeDisponivel(0)
                .disponivel(FALSE)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .categoriaNome("Quadrinhos")
                .imagemPrincipal("https://picsum.photos/200")
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
        Page<ListarMeuAnuncioResponse> pagina = new PageImpl<>(List.of(item));

        when(listarMeusAnunciosService.listar(any(Pageable.class))).thenReturn(pagina);

        // Act & Assert
        mockMvc.perform(get("/anuncios/meus")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(5L))
                .andExpect(jsonPath("$.content[0].disponivel").value(false))
                .andExpect(jsonPath("$.content[0].quantidadeDisponivel").value(0))
                .andExpect(jsonPath("$.content[0].imagemPrincipal").value("https://picsum.photos/200"));

        verify(listarMeusAnunciosService).listar(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar listar anúncios administrativos sem autenticação")
    void deveRetornar401AoListarAnunciosAdminSemAutenticacao() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/anuncios/admin"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(listarAnunciosAdminService);
    }

    @Test
    @DisplayName("Deve retornar 403 quando usuário comum tentar acessar listagem administrativa de anúncios")
    void deveRetornar403AoListarAnunciosAdminSemPermissao() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/anuncios/admin")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(listarAnunciosAdminService);
    }

    @Test
    @DisplayName("Deve listar todos os anúncios da plataforma para usuário ADMIN")
    void deveListarAnunciosAdminComPermissaoAdmin() throws Exception {

        // Arrange
        ListarAnuncioAdminResponse item = ListarAnuncioAdminResponse.builder()
                .id(1L)
                .nome("Action Figure Goku SSJ")
                .preco(BigDecimal.valueOf(299.90))
                .quantidadeDisponivel(2)
                .disponivel(TRUE)
                .categoriaNome("Figuras colecionáveis")
                .vendedorId(3L)
                .vendedorNome("Vendedor GeekHub")
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
        Page<ListarAnuncioAdminResponse> pagina = new PageImpl<>(List.of(item));

        when(listarAnunciosAdminService.listar(any(Pageable.class))).thenReturn(pagina);

        // Act & Assert
        mockMvc.perform(get("/anuncios/admin")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].vendedorId").value(3L))
                .andExpect(jsonPath("$.content[0].vendedorNome").value("Vendedor GeekHub"));

        verify(listarAnunciosAdminService).listar(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve listar catálogo público de anúncios sem autenticação e retornar 200 OK")
    void deveListarCatalogoPublicamenteSemAutenticacao() throws Exception {

        // Arrange
        ListarAnuncioResponse item = ListarAnuncioResponse.builder()
                .id(1L)
                .nome("Action figure Naruto 1ª edição")
                .preco(BigDecimal.valueOf(1850.00))
                .imagemPrincipal("https://picsum.photos/200")
                .categoriaId(1L)
                .categoriaNome("Figuras colecionáveis")
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .quantidadeDisponivel(3)
                .vendedorId(3L)
                .vendedorNome("Vendedor GeekHub")
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
        Page<ListarAnuncioResponse> pagina = new PageImpl<>(List.of(item));

        when(pesquisarAnunciosService.pesquisar(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(pagina);

        // Act & Assert
        mockMvc.perform(get("/anuncios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nome").value("Action figure Naruto 1ª edição"))
                .andExpect(jsonPath("$.content[0].imagemPrincipal").value("https://picsum.photos/200"))
                .andExpect(jsonPath("$.content[0].vendedorNome").value("Vendedor GeekHub"));

        verify(pesquisarAnunciosService).pesquisar(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve pesquisar catálogo combinando todos os filtros opcionais")
    void devePesquisarCatalogoComFiltrosCombinados() throws Exception {

        // Arrange
        ListarAnuncioResponse item = ListarAnuncioResponse.builder()
                .id(3L)
                .nome("Estátua Kakashi Hatake 18cm")
                .preco(BigDecimal.valueOf(329.90))
                .imagemPrincipal("https://picsum.photos/200")
                .categoriaId(1L)
                .categoriaNome("Figuras colecionáveis")
                .condicao(USADO)
                .conservacao(BOM)
                .quantidadeDisponivel(2)
                .vendedorId(3L)
                .vendedorNome("Vendedor GeekHub")
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
        Page<ListarAnuncioResponse> pagina = new PageImpl<>(List.of(item));

        when(pesquisarAnunciosService.pesquisar(
                eq("kakashi"),
                eq(1L),
                eq(BigDecimal.valueOf(200)),
                eq(BigDecimal.valueOf(400)),
                eq(USADO),
                eq(BOM),
                eq(3L),
                any(Pageable.class)))
                .thenReturn(pagina);

        // Act & Assert
        mockMvc.perform(get("/anuncios")
                        .param("nome", "kakashi")
                        .param("categoriaId", "1")
                        .param("precoMin", "200")
                        .param("precoMax", "400")
                        .param("condicao", "USADO")
                        .param("conservacao", "BOM")
                        .param("vendedorId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3L))
                .andExpect(jsonPath("$.content[0].condicao").value("USADO"))
                .andExpect(jsonPath("$.content[0].conservacao").value("BOM"));
    }

    @Test
    @DisplayName("Deve retornar página vazia com 200 OK quando nenhum anúncio corresponder aos filtros")
    void deveRetornarPaginaVaziaNoCatalogoSemResultados() throws Exception {

        // Arrange
        when(pesquisarAnunciosService.pesquisar(
                eq("inexistente"), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act & Assert
        mockMvc.perform(get("/anuncios").param("nome", "inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando precoMin for maior que precoMax")
    void deveRetornar400QuandoFaixaDePrecoInvalida() throws Exception {

        // Arrange
        when(pesquisarAnunciosService.pesquisar(
                isNull(), isNull(), eq(BigDecimal.valueOf(500)), eq(BigDecimal.valueOf(100)),
                isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenThrow(new NegocioException("Preço mínimo não pode ser maior que o preço máximo"));

        // Act & Assert
        mockMvc.perform(get("/anuncios")
                        .param("precoMin", "500")
                        .param("precoMax", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Preço mínimo não pode ser maior que o preço máximo"));
    }
}
