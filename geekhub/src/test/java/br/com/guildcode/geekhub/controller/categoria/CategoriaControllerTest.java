package br.com.guildcode.geekhub.controller.categoria;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.CadastrarCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.DetalharCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.EditarCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.ListarCategoriaResponse;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.service.categoria.CadastrarCategoriaService;
import br.com.guildcode.geekhub.service.categoria.DetalharCategoriaService;
import br.com.guildcode.geekhub.service.categoria.EditarCategoriaService;
import br.com.guildcode.geekhub.service.categoria.ListarCategoriasService;
import br.com.guildcode.geekhub.service.categoria.RemoverCategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

@WebMvcTest(CategoriaController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CadastrarCategoriaService cadastrarCategoriaService;

    @MockitoBean
    private ListarCategoriasService listarCategoriasService;

    @MockitoBean
    private DetalharCategoriaService detalharCategoriaService;

    @MockitoBean
    private EditarCategoriaService editarCategoriaService;

    @MockitoBean
    private RemoverCategoriaService removerCategoriaService;

    @Test
    @DisplayName("Deve permitir listar categorias publicamente sem autenticação")
    void deveListarCategoriasPublicamente() throws Exception {

        // Arrange
        ListarCategoriaResponse item = ListarCategoriaResponse.builder()
                .id(1L)
                .nome("Figuras colecionáveis")
                .descricao("Action figures")
                .ativa(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();
        Page<ListarCategoriaResponse> pagina = new PageImpl<>(List.of(item));

        when(listarCategoriasService.listar(eq(TRUE), any(Pageable.class))).thenReturn(pagina);

        // Act & Assert
        mockMvc.perform(get("/categorias").param("ativa", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nome").value("Figuras colecionáveis"))
                .andExpect(jsonPath("$.content[0].ativa").value(true));

        verify(listarCategoriasService).listar(eq(TRUE), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve permitir detalhar categoria por ID publicamente sem autenticação")
    void deveDetalharCategoriaPublicamente() throws Exception {

        // Arrange
        DetalharCategoriaResponse response = DetalharCategoriaResponse.builder()
                .id(1L)
                .nome("Figuras colecionáveis")
                .descricao("Action figures")
                .ativa(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        when(detalharCategoriaService.detalhar(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Figuras colecionáveis"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao detalhar categoria inexistente")
    void deveRetornar404AoDetalharCategoriaInexistente() throws Exception {

        // Arrange
        when(detalharCategoriaService.detalhar(99L))
                .thenThrow(new RecursoNaoEncontradoException("Categoria não encontrada"));

        // Act & Assert
        mockMvc.perform(get("/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar cadastrar categoria sem autenticação")
    void deveRetornar401AoCadastrarSemAutenticacao() throws Exception {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequest.builder()
                .nome("Board Games")
                .descricao("Jogos de tabuleiro")
                .build();

        // Act & Assert
        mockMvc.perform(post("/categorias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cadastrarCategoriaService);
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar cadastrar categoria sem permissão ADMIN")
    void deveRetornar403AoCadastrarSemPermissaoAdmin() throws Exception {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequest.builder()
                .nome("Board Games")
                .descricao("Jogos de tabuleiro")
                .build();

        // Act & Assert
        mockMvc.perform(post("/categorias")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(cadastrarCategoriaService);
    }

    @Test
    @DisplayName("Deve cadastrar categoria com permissão ADMIN e retornar 201 Created")
    void deveCadastrarCategoriaComAdmin() throws Exception {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequest.builder()
                .nome("Board Games")
                .descricao("Jogos de tabuleiro")
                .build();
        CadastrarCategoriaResponse response = CadastrarCategoriaResponse.builder()
                .id(6L)
                .nome("Board Games")
                .descricao("Jogos de tabuleiro")
                .ativa(TRUE)
                .criadoEm(LocalDateTime.of(2026, 11, 15, 17, 0))
                .build();

        when(cadastrarCategoriaService.cadastrar(any(CadastrarCategoriaRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/categorias")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.nome").value("Board Games"))
                .andExpect(jsonPath("$.ativa").value(true));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar cadastrar categoria com dados inválidos")
    void deveRetornar400AoCadastrarComDadosInvalidos() throws Exception {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequest.builder()
                .nome("")
                .descricao("Descrição")
                .build();

        // Act & Assert
        mockMvc.perform(post("/categorias")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(cadastrarCategoriaService);
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar cadastrar categoria com nome duplicado")
    void deveRetornar409AoCadastrarComNomeDuplicado() throws Exception {

        // Arrange
        CadastrarCategoriaRequest request = CadastrarCategoriaRequest.builder()
                .nome("Figuras colecionáveis")
                .descricao("Duplicada")
                .build();

        when(cadastrarCategoriaService.cadastrar(any(CadastrarCategoriaRequest.class)))
                .thenThrow(new ConflitoException("Categoria já cadastrada"));

        // Act & Assert
        mockMvc.perform(post("/categorias")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Categoria já cadastrada"));
    }

    @Test
    @DisplayName("Deve editar categoria com permissão ADMIN e retornar 200 OK")
    void deveEditarCategoriaComAdmin() throws Exception {

        // Arrange
        EditarCategoriaRequest request = EditarCategoriaRequest.builder()
                .nome("Board Games")
                .descricao("Jogos de tabuleiro e cartas")
                .ativa(FALSE)
                .build();
        EditarCategoriaResponse response = EditarCategoriaResponse.builder()
                .id(1L)
                .nome("Board Games")
                .descricao("Jogos de tabuleiro e cartas")
                .ativa(FALSE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        when(editarCategoriaService.editar(eq(1L), any(EditarCategoriaRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/categorias/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Board Games"))
                .andExpect(jsonPath("$.ativa").value(false));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar editar categoria sem permissão ADMIN")
    void deveRetornar403AoEditarSemPermissaoAdmin() throws Exception {

        // Arrange
        EditarCategoriaRequest request = EditarCategoriaRequest.builder()
                .nome("Board Games")
                .descricao("Descrição")
                .ativa(TRUE)
                .build();

        // Act & Assert
        mockMvc.perform(put("/categorias/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(editarCategoriaService);
    }

    @Test
    @DisplayName("Deve remover categoria sem anúncios vinculados com permissão ADMIN e retornar 204")
    void deveRemoverCategoriaComAdmin() throws Exception {

        // Arrange
        doNothing().when(removerCategoriaService).remover(3L);

        // Act & Assert
        mockMvc.perform(delete("/categorias/3")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isNoContent());

        verify(removerCategoriaService).remover(3L);
    }

    @Test
    @DisplayName("Deve retornar 409 ao tentar remover categoria com anúncios vinculados")
    void deveRetornar409AoRemoverCategoriaComAnuncios() throws Exception {

        // Arrange
        doThrow(new ConflitoException("Não é possível remover categoria com anúncios vinculados"))
                .when(removerCategoriaService).remover(1L);

        // Act & Assert
        mockMvc.perform(delete("/categorias/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Não é possível remover categoria com anúncios vinculados"));
    }
}
