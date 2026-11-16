package br.com.guildcode.geekhub.controller.avaliacao;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.AutorAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.CadastrarAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.DetalharAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.EditarAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.ListarAvaliacaoResponse;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.avaliacao.CadastrarAvaliacaoRequestFactory;
import br.com.guildcode.geekhub.factory.avaliacao.EditarAvaliacaoRequestFactory;
import br.com.guildcode.geekhub.service.avaliacao.CadastrarAvaliacaoService;
import br.com.guildcode.geekhub.service.avaliacao.DetalharAvaliacaoService;
import br.com.guildcode.geekhub.service.avaliacao.EditarAvaliacaoService;
import br.com.guildcode.geekhub.service.avaliacao.ListarAvaliacoesService;
import br.com.guildcode.geekhub.service.avaliacao.RemoverAvaliacaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

@WebMvcTest(AvaliacaoController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CadastrarAvaliacaoService cadastrarAvaliacaoService;

    @MockitoBean
    private ListarAvaliacoesService listarAvaliacoesService;

    @MockitoBean
    private DetalharAvaliacaoService detalharAvaliacaoService;

    @MockitoBean
    private EditarAvaliacaoService editarAvaliacaoService;

    @MockitoBean
    private RemoverAvaliacaoService removerAvaliacaoService;

    @Test
    @DisplayName("Deve retornar 401 ao tentar cadastrar avaliação sem autenticação")
    void deveRetornar401AoCadastrarAvaliacaoSemAutenticacao() throws Exception {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();

        // Act & Assert
        mockMvc.perform(post("/avaliacoes")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(cadastrarAvaliacaoService);
    }

    @Test
    @DisplayName("Deve cadastrar avaliação válida para usuário autenticado e retornar 201 Created")
    void deveCadastrarAvaliacaoComSucesso() throws Exception {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        LocalDateTime agora = LocalDateTime.of(2026, 11, 15, 21, 0);
        CadastrarAvaliacaoResponse response = CadastrarAvaliacaoResponse.builder()
                .id(2L)
                .pedidoId(request.pedidoId())
                .vendedorId(3L)
                .autorId(2L)
                .autorNome("Usuario GeekHub")
                .nota(request.nota())
                .comentario(request.comentario())
                .criadoEm(agora)
                .build();

        when(cadastrarAvaliacaoService.cadastrar(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/avaliacoes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.pedidoId").value(3L))
                .andExpect(jsonPath("$.vendedorId").value(3L))
                .andExpect(jsonPath("$.autorId").value(2L))
                .andExpect(jsonPath("$.autorNome").value("Usuario GeekHub"))
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.comentario").value(request.comentario()));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao enviar nota fora do intervalo 1..5 ou pedidoId nulo")
    void deveRetornar400AoCadastrarComBeanValidationInvalida() throws Exception {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequest.builder()
                .pedidoId(null)
                .nota(6)
                .comentario("Inválida")
                .build();

        // Act & Assert
        mockMvc.perform(post("/avaliacoes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes").isArray());

        verifyNoInteractions(cadastrarAvaliacaoService);
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar avaliar pedido não concluído")
    void deveRetornar400QuandoPedidoNaoConcluido() throws Exception {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        when(cadastrarAvaliacaoService.cadastrar(request))
                .thenThrow(new NegocioException("Somente pedidos concluídos podem ser avaliados"));

        // Act & Assert
        mockMvc.perform(post("/avaliacoes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Somente pedidos concluídos podem ser avaliados"));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar avaliar pedido de outro comprador")
    void deveRetornar403QuandoAvaliarPedidoDeOutroComprador() throws Exception {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        when(cadastrarAvaliacaoService.cadastrar(request))
                .thenThrow(new AccessDeniedException("Você não possui permissão para avaliar este pedido"));

        // Act & Assert
        mockMvc.perform(post("/avaliacoes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar avaliar pedido inexistente")
    void deveRetornar404QuandoPedidoNaoEncontrado() throws Exception {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequest.builder()
                .pedidoId(999L)
                .nota(5)
                .build();
        when(cadastrarAvaliacaoService.cadastrar(request))
                .thenThrow(new RecursoNaoEncontradoException("Pedido não encontrado"));

        // Act & Assert
        mockMvc.perform(post("/avaliacoes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Pedido não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict ao tentar avaliar pedido já avaliado")
    void deveRetornar409QuandoPedidoJaAvaliado() throws Exception {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        when(cadastrarAvaliacaoService.cadastrar(request))
                .thenThrow(new ConflitoException("Este pedido já foi avaliado"));

        // Act & Assert
        mockMvc.perform(post("/avaliacoes")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.erro").value("Este pedido já foi avaliado"));
    }

    @Test
    @DisplayName("Deve listar avaliações publicamente sem autenticação utilizando ordenação padrão")
    void deveListarAvaliacoesPublicamenteSemFiltro() throws Exception {
        // Arrange
        LocalDateTime criadoEm = LocalDateTime.of(2026, 11, 15, 20, 0);
        ListarAvaliacaoResponse item = ListarAvaliacaoResponse.builder()
                .id(1L)
                .pedidoId(4L)
                .vendedorId(3L)
                .autor(AutorAvaliacaoResponse.builder()
                        .id(2L)
                        .nome("Usuario GeekHub")
                        .imagemPerfil("https://picsum.photos/200")
                        .build())
                .nota(5)
                .comentario("Excelente vendedor!")
                .criadoEm(criadoEm)
                .build();
        Pageable defaultPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "criadoEm"));

        when(listarAvaliacoesService.listar(isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item), defaultPageable, 1));

        // Act & Assert
        mockMvc.perform(get("/avaliacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].pedidoId").value(4L))
                .andExpect(jsonPath("$.content[0].vendedorId").value(3L))
                .andExpect(jsonPath("$.content[0].autor.id").value(2L))
                .andExpect(jsonPath("$.content[0].autor.nome").value("Usuario GeekHub"))
                .andExpect(jsonPath("$.content[0].nota").value(5));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(listarAvaliacoesService).listar(isNull(), pageableCaptor.capture());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.Direction.DESC, pageableCaptor.getValue().getSort().getOrderFor("criadoEm").getDirection());
    }

    @Test
    @DisplayName("Deve listar avaliações publicamente filtrando por vendedorId")
    void deveListarAvaliacoesFiltradasPorVendedor() throws Exception {
        // Arrange
        when(listarAvaliacoesService.listar(eq(3L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        // Act & Assert
        mockMvc.perform(get("/avaliacoes").param("vendedorId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(listarAvaliacoesService).listar(eq(3L), any(Pageable.class));
    }

    @Test
    @DisplayName("Deve detalhar avaliação por id publicamente sem autenticação")
    void deveDetalharAvaliacaoPublicamente() throws Exception {
        // Arrange
        DetalharAvaliacaoResponse response = DetalharAvaliacaoResponse.builder()
                .id(1L)
                .pedidoId(4L)
                .vendedorId(3L)
                .autor(AutorAvaliacaoResponse.builder()
                        .id(2L)
                        .nome("Usuario GeekHub")
                        .imagemPerfil("https://picsum.photos/200")
                        .build())
                .nota(5)
                .comentario("Excelente vendedor!")
                .criadoEm(LocalDateTime.of(2026, 11, 15, 20, 0))
                .build();

        when(detalharAvaliacaoService.detalhar(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/avaliacoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.vendedorId").value(3L))
                .andExpect(jsonPath("$.autor.nome").value("Usuario GeekHub"))
                .andExpect(jsonPath("$.nota").value(5));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao detalhar avaliação inexistente")
    void deveRetornar404AoDetalharAvaliacaoInexistente() throws Exception {
        // Arrange
        when(detalharAvaliacaoService.detalhar(999L))
                .thenThrow(new RecursoNaoEncontradoException("Avaliação não encontrada"));

        // Act & Assert
        mockMvc.perform(get("/avaliacoes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Avaliação não encontrada"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar editar avaliação sem autenticação")
    void deveRetornar401AoEditarAvaliacaoSemAutenticacao() throws Exception {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();

        // Act & Assert
        mockMvc.perform(put("/avaliacoes/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(editarAvaliacaoService);
    }

    @Test
    @DisplayName("Deve editar avaliação do próprio autor e retornar 200 OK")
    void deveEditarAvaliacaoComSucesso() throws Exception {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();
        EditarAvaliacaoResponse response = EditarAvaliacaoResponse.builder()
                .id(1L)
                .pedidoId(4L)
                .vendedorId(3L)
                .autorId(2L)
                .autorNome("Usuario GeekHub")
                .nota(request.nota())
                .comentario(request.comentario())
                .criadoEm(LocalDateTime.of(2026, 11, 15, 20, 0))
                .build();

        when(editarAvaliacaoService.editar(1L, request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/avaliacoes/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentario").value(request.comentario()));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar editar avaliação com nota inválida")
    void deveRetornar400AoEditarComNotaInvalida() throws Exception {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequest.builder()
                .nota(0)
                .comentario("Inválida")
                .build();

        // Act & Assert
        mockMvc.perform(put("/avaliacoes/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes").isArray());

        verifyNoInteractions(editarAvaliacaoService);
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando usuário não autor tentar editar avaliação")
    void deveRetornar403AoEditarAvaliacaoDeOutroAutor() throws Exception {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();
        when(editarAvaliacaoService.editar(1L, request))
                .thenThrow(new AccessDeniedException("Você não possui permissão para editar esta avaliação"));

        // Act & Assert
        mockMvc.perform(put("/avaliacoes/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar editar avaliação inexistente")
    void deveRetornar404AoEditarAvaliacaoInexistente() throws Exception {
        // Arrange
        EditarAvaliacaoRequest request = EditarAvaliacaoRequestFactory.get();
        when(editarAvaliacaoService.editar(999L, request))
                .thenThrow(new RecursoNaoEncontradoException("Avaliação não encontrada"));

        // Act & Assert
        mockMvc.perform(put("/avaliacoes/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Avaliação não encontrada"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar remover avaliação sem autenticação")
    void deveRetornar401AoRemoverAvaliacaoSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/avaliacoes/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(removerAvaliacaoService);
    }

    @Test
    @DisplayName("Deve remover avaliação com sucesso e retornar 204 No Content")
    void deveRemoverAvaliacaoComSucesso() throws Exception {
        // Arrange
        doNothing().when(removerAvaliacaoService).remover(1L);

        // Act & Assert
        mockMvc.perform(delete("/avaliacoes/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isNoContent());

        verify(removerAvaliacaoService).remover(1L);
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden quando usuário sem permissão tentar remover avaliação")
    void deveRetornar403AoRemoverAvaliacaoSemPermissao() throws Exception {
        // Arrange
        doThrow(new AccessDeniedException("Você não possui permissão para remover esta avaliação"))
                .when(removerAvaliacaoService)
                .remover(1L);

        // Act & Assert
        mockMvc.perform(delete("/avaliacoes/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar remover avaliação inexistente")
    void deveRetornar404AoRemoverAvaliacaoInexistente() throws Exception {
        // Arrange
        doThrow(new RecursoNaoEncontradoException("Avaliação não encontrada"))
                .when(removerAvaliacaoService)
                .remover(999L);

        // Act & Assert
        mockMvc.perform(delete("/avaliacoes/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Avaliação não encontrada"));
    }
}
