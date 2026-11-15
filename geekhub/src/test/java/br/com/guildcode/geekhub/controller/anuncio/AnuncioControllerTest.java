package br.com.guildcode.geekhub.controller.anuncio;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.CadastrarAnuncioRequestFactory;
import br.com.guildcode.geekhub.factory.anuncio.EditarAnuncioRequestFactory;
import br.com.guildcode.geekhub.service.anuncio.CadastrarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.EditarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.RemoverAnuncioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
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
}
