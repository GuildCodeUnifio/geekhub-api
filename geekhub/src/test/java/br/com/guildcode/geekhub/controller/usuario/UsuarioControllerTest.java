package br.com.guildcode.geekhub.controller.usuario;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.usuario.dto.request.AtualizarPermissaoUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.dto.response.AnuncioPerfilVendedorResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.AtualizarPermissaoUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.AvaliacaoPerfilVendedorResponse;
import br.com.guildcode.geekhub.controller.usuario.dto.response.PerfilVendedorResponse;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.usuario.AtualizarPermissaoUsuarioRequestFactory;
import br.com.guildcode.geekhub.service.usuario.AtualizarPermissaoUsuarioService;
import br.com.guildcode.geekhub.service.usuario.CadastrarUsuarioService;
import br.com.guildcode.geekhub.service.usuario.DetalharUsuarioService;
import br.com.guildcode.geekhub.service.usuario.EditarUsuarioService;
import br.com.guildcode.geekhub.service.usuario.ListarUsuariosService;
import br.com.guildcode.geekhub.service.usuario.PerfilVendedorService;
import br.com.guildcode.geekhub.service.usuario.RemoverUsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CadastrarUsuarioService cadastrarUsuarioService;

    @MockitoBean
    private ListarUsuariosService listarUsuariosService;

    @MockitoBean
    private DetalharUsuarioService detalharUsuarioService;

    @MockitoBean
    private EditarUsuarioService editarUsuarioService;

    @MockitoBean
    private RemoverUsuarioService removerUsuarioService;

    @MockitoBean
    private PerfilVendedorService perfilVendedorService;

    @MockitoBean
    private AtualizarPermissaoUsuarioService atualizarPermissaoUsuarioService;

    @Test
    @DisplayName("Deve consultar perfil público do vendedor sem autenticação expondo apenas dados públicos")
    void deveConsultarPerfilVendedorPublicamenteComSucesso() throws Exception {
        // Arrange
        LocalDateTime membroDesde = LocalDateTime.of(2026, 11, 13, 18, 0);
        LocalDateTime criadoEm = LocalDateTime.of(2026, 11, 13, 18, 50);

        PerfilVendedorResponse response = PerfilVendedorResponse.builder()
                .id(3L)
                .nome("Vendedor GeekHub")
                .imagemPerfil("https://picsum.photos/200")
                .membroDesde(membroDesde)
                .notaMedia(5.0)
                .quantidadeAvaliacoes(1L)
                .quantidadeProdutos(3L)
                .quantidadeVendas(2L)
                .avaliacoes(List.of(AvaliacaoPerfilVendedorResponse.builder()
                        .id(1L)
                        .pedidoId(4L)
                        .autorId(2L)
                        .autorNome("Usuario GeekHub")
                        .autorImagemPerfil("https://picsum.photos/200")
                        .nota(5)
                        .comentario("Produto muito bem embalado e entrega rápida!")
                        .criadoEm(criadoEm)
                        .build()))
                .anuncios(List.of(AnuncioPerfilVendedorResponse.builder()
                        .id(1L)
                        .nome("Action figure Naruto 1ª edição")
                        .preco(new BigDecimal("1850.00"))
                        .imagemPrincipal("https://picsum.photos/200")
                        .categoriaId(1L)
                        .categoriaNome("Figuras colecionáveis")
                        .condicao(NOVO)
                        .conservacao(EXCELENTE)
                        .quantidadeDisponivel(3)
                        .criadoEm(LocalDateTime.of(2026, 11, 13, 18, 10))
                        .build()))
                .build();

        when(perfilVendedorService.consultar(3L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/usuarios/3/perfil-vendedor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.nome").value("Vendedor GeekHub"))
                .andExpect(jsonPath("$.imagemPerfil").value("https://picsum.photos/200"))
                .andExpect(jsonPath("$.notaMedia").value(5.0))
                .andExpect(jsonPath("$.quantidadeAvaliacoes").value(1))
                .andExpect(jsonPath("$.quantidadeProdutos").value(3))
                .andExpect(jsonPath("$.quantidadeVendas").value(2))
                .andExpect(jsonPath("$.avaliacoes[0].id").value(1L))
                .andExpect(jsonPath("$.avaliacoes[0].autorNome").value("Usuario GeekHub"))
                .andExpect(jsonPath("$.anuncios[0].id").value(1L))
                .andExpect(jsonPath("$.anuncios[0].nome").value("Action figure Naruto 1ª edição"))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());

        verify(perfilVendedorService).consultar(3L);
    }

    @Test
    @DisplayName("Deve retornar 200 OK com notaMedia nula e listas vazias para vendedor sem histórico")
    void deveRetornarPerfilVendedorSemHistorico() throws Exception {
        // Arrange
        PerfilVendedorResponse response = PerfilVendedorResponse.builder()
                .id(1L)
                .nome("Admin GeekHub")
                .imagemPerfil(null)
                .membroDesde(LocalDateTime.of(2026, 11, 13, 18, 0))
                .notaMedia(null)
                .quantidadeAvaliacoes(0L)
                .quantidadeProdutos(0L)
                .quantidadeVendas(0L)
                .avaliacoes(List.of())
                .anuncios(List.of())
                .build();

        when(perfilVendedorService.consultar(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/usuarios/1/perfil-vendedor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.notaMedia").value(nullValue()))
                .andExpect(jsonPath("$.quantidadeAvaliacoes").value(0))
                .andExpect(jsonPath("$.quantidadeProdutos").value(0))
                .andExpect(jsonPath("$.quantidadeVendas").value(0))
                .andExpect(jsonPath("$.avaliacoes").isEmpty())
                .andExpect(jsonPath("$.anuncios").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao consultar perfil de vendedor inexistente")
    void deveRetornar404QuandoVendedorNaoEncontrado() throws Exception {
        // Arrange
        when(perfilVendedorService.consultar(99999L))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/usuarios/99999/perfil-vendedor"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve continuar exigindo autenticação ao detalhar usuário diretamente em GET /usuarios/{id}")
    void deveExigirAutenticacaoAoDetalharUsuario() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/usuarios/3"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(detalharUsuarioService);
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao atualizar permissão de administrador quando usuário possuir SCOPE_ADMIN")
    void deveAtualizarPermissaoQuandoAdmin() throws Exception {
        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();
        AtualizarPermissaoUsuarioResponse response = AtualizarPermissaoUsuarioResponse.builder()
                .id(2L)
                .nome("Usuario GeekHub")
                .email("usuario@geekhub.com")
                .admin(true)
                .permissoes(List.of("USUARIO", "ADMIN"))
                .build();

        when(atualizarPermissaoUsuarioService.atualizar(eq(2L), any(AtualizarPermissaoUsuarioRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/usuarios/2/permissao")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.admin").value(true))
                .andExpect(jsonPath("$.permissoes[0]").value("USUARIO"))
                .andExpect(jsonPath("$.permissoes[1]").value("ADMIN"));

        verify(atualizarPermissaoUsuarioService).atualizar(eq(2L), any(AtualizarPermissaoUsuarioRequest.class));
    }

    @Test
    @DisplayName("Deve retornar 401 Unauthorized ao tentar atualizar permissão sem autenticação")
    void deveRetornar401AoAtualizarPermissaoSemAutenticacao() throws Exception {
        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();

        // Act & Assert
        mockMvc.perform(put("/usuarios/2/permissao")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(atualizarPermissaoUsuarioService);
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar atualizar permissão sem SCOPE_ADMIN")
    void deveRetornar403AoAtualizarPermissaoSemAdmin() throws Exception {
        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();

        // Act & Assert
        mockMvc.perform(put("/usuarios/2/permissao")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(atualizarPermissaoUsuarioService);
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando admin for nulo ao atualizar permissão")
    void deveRetornar400QuandoAdminNulo() throws Exception {
        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequest.builder()
                .admin(null)
                .build();

        // Act & Assert
        mockMvc.perform(put("/usuarios/2/permissao")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(atualizarPermissaoUsuarioService);
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando admin tentar alterar a própria permissão")
    void deveRetornar400QuandoAlterarPropriaPermissao() throws Exception {
        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.rebaixarAdmin();
        when(atualizarPermissaoUsuarioService.atualizar(eq(1L), any(AtualizarPermissaoUsuarioRequest.class)))
                .thenThrow(new NegocioException("Não é possível alterar a própria permissão de administrador"));

        // Act & Assert
        mockMvc.perform(put("/usuarios/1/permissao")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Não é possível alterar a própria permissão de administrador"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found quando usuário não for encontrado ao atualizar permissão")
    void deveRetornar404QuandoUsuarioNaoEncontradoAoAtualizarPermissao() throws Exception {
        // Arrange
        AtualizarPermissaoUsuarioRequest request = AtualizarPermissaoUsuarioRequestFactory.promoverAdmin();
        when(atualizarPermissaoUsuarioService.atualizar(eq(999L), any(AtualizarPermissaoUsuarioRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(put("/usuarios/999/permissao")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_ADMIN")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Usuário não encontrado"));
    }
}
