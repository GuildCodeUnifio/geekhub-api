package br.com.guildcode.geekhub.controller.carrinho;

import br.com.guildcode.geekhub.config.CorsConfig;
import br.com.guildcode.geekhub.config.SecurityConfig;
import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.request.EditarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.AdicionarItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ConsultarCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.DetalharItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.EditarItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ItemCarrinhoDetalheResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ListarItemCarrinhoResponse;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.carrinho.AdicionarItemCarrinhoRequestFactory;
import br.com.guildcode.geekhub.factory.carrinho.EditarItemCarrinhoRequestFactory;
import br.com.guildcode.geekhub.service.carrinho.AdicionarItemCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.ConsultarCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.DetalharItemCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.EditarItemCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.LimparCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.ListarItensCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.RemoverItemCarrinhoService;
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
import java.util.Collections;
import java.util.List;

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

@WebMvcTest(CarrinhoController.class)
@Import({SecurityConfig.class, CorsConfig.class, AopAutoConfiguration.class})
class CarrinhoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdicionarItemCarrinhoService adicionarItemCarrinhoService;

    @MockitoBean
    private ConsultarCarrinhoService consultarCarrinhoService;

    @MockitoBean
    private ListarItensCarrinhoService listarItensCarrinhoService;

    @MockitoBean
    private DetalharItemCarrinhoService detalharItemCarrinhoService;

    @MockitoBean
    private EditarItemCarrinhoService editarItemCarrinhoService;

    @MockitoBean
    private RemoverItemCarrinhoService removerItemCarrinhoService;

    @MockitoBean
    private LimparCarrinhoService limparCarrinhoService;

    @Test
    @DisplayName("Deve retornar 401 ao tentar adicionar item ao carrinho sem autenticação")
    void deveRetornar401AoAdicionarItemSemAutenticacao() throws Exception {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();

        // Act & Assert
        mockMvc.perform(post("/carrinho/itens")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(adicionarItemCarrinhoService);
    }

    @Test
    @DisplayName("Deve adicionar item ao carrinho e retornar 201 Created")
    void deveAdicionarItemComSucesso() throws Exception {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();
        AdicionarItemCarrinhoResponse response = AdicionarItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(1L)
                .nomeProduto("Action figure Naruto 1ª edição")
                .precoUnitario(new BigDecimal("1850.00"))
                .quantidade(2)
                .subtotal(new BigDecimal("3700.00"))
                .criadoEm(LocalDateTime.of(2026, 11, 15, 19, 0))
                .build();

        when(adicionarItemCarrinhoService.adicionar(any(AdicionarItemCarrinhoRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/carrinho/itens")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.anuncioId").value(1L))
                .andExpect(jsonPath("$.nomeProduto").value("Action figure Naruto 1ª edição"))
                .andExpect(jsonPath("$.quantidade").value(2))
                .andExpect(jsonPath("$.subtotal").value(3700.00));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar adicionar item com dados inválidos no body")
    void deveRetornar400QuandoDadosAdicaoInvalidos() throws Exception {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequest.builder()
                .anuncioId(null)
                .quantidade(0)
                .build();

        // Act & Assert
        mockMvc.perform(post("/carrinho/itens")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(adicionarItemCarrinhoService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar adicionar item indisponível ou que exceda estoque")
    void deveRetornar400QuandoRegraNegocioViolarDisponibilidade() throws Exception {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();
        when(adicionarItemCarrinhoService.adicionar(any(AdicionarItemCarrinhoRequest.class)))
                .thenThrow(new NegocioException("Quantidade solicitada excede o estoque disponível do anúncio"));

        // Act & Assert
        mockMvc.perform(post("/carrinho/itens")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Quantidade solicitada excede o estoque disponível do anúncio"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar adicionar anúncio inexistente ao carrinho")
    void deveRetornar404QuandoAnuncioNaoExistir() throws Exception {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequest.builder()
                .anuncioId(999L)
                .quantidade(1)
                .build();
        when(adicionarItemCarrinhoService.adicionar(any(AdicionarItemCarrinhoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Anúncio não encontrado"));

        // Act & Assert
        mockMvc.perform(post("/carrinho/itens")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Anúncio não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar consultar carrinho sem autenticação")
    void deveRetornar401AoConsultarCarrinhoSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/carrinho"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(consultarCarrinhoService);
    }

    @Test
    @DisplayName("Deve consultar carrinho do comprador autenticado com total calculado e retornar 200 OK")
    void deveConsultarCarrinhoComItensERetornar200() throws Exception {
        // Arrange
        ItemCarrinhoDetalheResponse item = ItemCarrinhoDetalheResponse.builder()
                .id(1L)
                .anuncioId(2L)
                .vendedorId(3L)
                .nome("Mangá Colecionável — Edição Especial")
                .imagem("https://picsum.photos/200")
                .precoUnitario(new BigDecimal("49.90"))
                .quantidade(2)
                .quantidadeDisponivel(5)
                .disponivel(TRUE)
                .subtotal(new BigDecimal("99.80"))
                .build();
        ConsultarCarrinhoResponse response = ConsultarCarrinhoResponse.builder()
                .compradorId(2L)
                .itens(List.of(item))
                .total(new BigDecimal("99.80"))
                .build();

        when(consultarCarrinhoService.consultar()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/carrinho")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compradorId").value(2L))
                .andExpect(jsonPath("$.itens[0].id").value(1L))
                .andExpect(jsonPath("$.itens[0].subtotal").value(99.80))
                .andExpect(jsonPath("$.total").value(99.80));
    }

    @Test
    @DisplayName("Deve consultar carrinho vazio retornando lista vazia e total zerado")
    void deveConsultarCarrinhoVazioERetornar200() throws Exception {
        // Arrange
        ConsultarCarrinhoResponse response = ConsultarCarrinhoResponse.builder()
                .compradorId(2L)
                .itens(Collections.emptyList())
                .total(new BigDecimal("0.00"))
                .build();

        when(consultarCarrinhoService.consultar()).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/carrinho")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compradorId").value(2L))
                .andExpect(jsonPath("$.itens").isEmpty())
                .andExpect(jsonPath("$.total").value(0.00));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar listar itens do carrinho sem autenticação")
    void deveRetornar401AoListarItensSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/carrinho/itens"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(listarItensCarrinhoService);
    }

    @Test
    @DisplayName("Deve listar itens paginados do carrinho do comprador autenticado e retornar 200 OK")
    void deveListarItensPaginadosComSucesso() throws Exception {
        // Arrange
        ListarItemCarrinhoResponse item = ListarItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(2L)
                .vendedorId(3L)
                .nomeProduto("Mangá Colecionável — Edição Especial")
                .imagem("https://picsum.photos/200")
                .precoUnitario(new BigDecimal("49.90"))
                .quantidade(2)
                .quantidadeDisponivel(5)
                .disponivel(TRUE)
                .subtotal(new BigDecimal("99.80"))
                .criadoEm(LocalDateTime.of(2026, 11, 13, 18, 10))
                .build();
        Page<ListarItemCarrinhoResponse> pagina = new PageImpl<>(List.of(item));

        when(listarItensCarrinhoService.listar(any(Pageable.class))).thenReturn(pagina);

        // Act & Assert
        mockMvc.perform(get("/carrinho/itens")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].nomeProduto").value("Mangá Colecionável — Edição Especial"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(listarItensCarrinhoService).listar(any(Pageable.class));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar detalhar item do carrinho sem autenticação")
    void deveRetornar401AoDetalharItemSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/carrinho/itens/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(detalharItemCarrinhoService);
    }

    @Test
    @DisplayName("Deve detalhar item do carrinho do comprador autenticado e retornar 200 OK")
    void deveDetalharItemComSucesso() throws Exception {
        // Arrange
        DetalharItemCarrinhoResponse response = DetalharItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(2L)
                .vendedorId(3L)
                .nomeProduto("Mangá Colecionável — Edição Especial")
                .imagem("https://picsum.photos/200")
                .precoUnitario(new BigDecimal("49.90"))
                .quantidade(2)
                .quantidadeDisponivel(5)
                .disponivel(TRUE)
                .subtotal(new BigDecimal("99.80"))
                .criadoEm(LocalDateTime.of(2026, 11, 13, 18, 10))
                .build();

        when(detalharItemCarrinhoService.detalhar(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/carrinho/itens/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.anuncioId").value(2L))
                .andExpect(jsonPath("$.nomeProduto").value("Mangá Colecionável — Edição Especial"))
                .andExpect(jsonPath("$.subtotal").value(99.80));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar detalhar item do carrinho de outro comprador")
    void deveRetornar403AoDetalharItemDeOutroComprador() throws Exception {
        // Arrange
        when(detalharItemCarrinhoService.detalhar(99L))
                .thenThrow(new AccessDeniedException("Você não possui permissão para acessar este item do carrinho"));

        // Act & Assert
        mockMvc.perform(get("/carrinho/itens/99")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("Acesso negado: você não possui permissão para realizar esta operação."));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar detalhar item inexistente do carrinho")
    void deveRetornar404AoDetalharItemInexistente() throws Exception {
        // Arrange
        when(detalharItemCarrinhoService.detalhar(999L))
                .thenThrow(new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/carrinho/itens/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Item do carrinho não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar editar quantidade de item sem autenticação")
    void deveRetornar401AoEditarItemSemAutenticacao() throws Exception {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();

        // Act & Assert
        mockMvc.perform(put("/carrinho/itens/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(editarItemCarrinhoService);
    }

    @Test
    @DisplayName("Deve atualizar quantidade do item do carrinho e retornar 200 OK")
    void deveEditarItemComSucesso() throws Exception {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();
        EditarItemCarrinhoResponse response = EditarItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(2L)
                .nomeProduto("Mangá Colecionável — Edição Especial")
                .precoUnitario(new BigDecimal("49.90"))
                .quantidade(3)
                .subtotal(new BigDecimal("149.70"))
                .build();

        when(editarItemCarrinhoService.editar(eq(1L), any(EditarItemCarrinhoRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/carrinho/itens/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.anuncioId").value(2L))
                .andExpect(jsonPath("$.quantidade").value(3))
                .andExpect(jsonPath("$.subtotal").value(149.70));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar editar item com quantidade menor ou igual a zero")
    void deveRetornar400AoEditarComQuantidadeInvalida() throws Exception {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequest.builder()
                .quantidade(0)
                .build();

        // Act & Assert
        mockMvc.perform(put("/carrinho/itens/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verifyNoInteractions(editarItemCarrinhoService);
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar editar item com quantidade acima do estoque disponível")
    void deveRetornar400AoEditarComQuantidadeAcimaDoEstoque() throws Exception {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequest.builder()
                .quantidade(99)
                .build();
        when(editarItemCarrinhoService.editar(eq(1L), any(EditarItemCarrinhoRequest.class)))
                .thenThrow(new NegocioException("Quantidade solicitada excede o estoque disponível do anúncio"));

        // Act & Assert
        mockMvc.perform(put("/carrinho/itens/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").value("Quantidade solicitada excede o estoque disponível do anúncio"));
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar editar item do carrinho de outro comprador")
    void deveRetornar403AoEditarItemDeOutroComprador() throws Exception {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();
        when(editarItemCarrinhoService.editar(eq(99L), any(EditarItemCarrinhoRequest.class)))
                .thenThrow(new AccessDeniedException("Você não possui permissão para acessar este item do carrinho"));

        // Act & Assert
        mockMvc.perform(put("/carrinho/itens/99")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("Acesso negado: você não possui permissão para realizar esta operação."));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar editar item inexistente do carrinho")
    void deveRetornar404AoEditarItemInexistente() throws Exception {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();
        when(editarItemCarrinhoService.editar(eq(999L), any(EditarItemCarrinhoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Act & Assert
        mockMvc.perform(put("/carrinho/itens/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO")))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Item do carrinho não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar remover item do carrinho sem autenticação")
    void deveRetornar401AoRemoverItemSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/carrinho/itens/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(removerItemCarrinhoService);
    }

    @Test
    @DisplayName("Deve remover item específico do carrinho e retornar 204 No Content")
    void deveRemoverItemComSucesso() throws Exception {
        // Arrange
        doNothing().when(removerItemCarrinhoService).remover(1L);

        // Act & Assert
        mockMvc.perform(delete("/carrinho/itens/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isNoContent());

        verify(removerItemCarrinhoService).remover(1L);
    }

    @Test
    @DisplayName("Deve retornar 403 ao tentar remover item do carrinho de outro comprador")
    void deveRetornar403AoRemoverItemDeOutroComprador() throws Exception {
        // Arrange
        doThrow(new AccessDeniedException("Você não possui permissão para acessar este item do carrinho"))
                .when(removerItemCarrinhoService).remover(99L);

        // Act & Assert
        mockMvc.perform(delete("/carrinho/itens/99")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("Acesso negado: você não possui permissão para realizar esta operação."));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar remover item inexistente do carrinho")
    void deveRetornar404AoRemoverItemInexistente() throws Exception {
        // Arrange
        doThrow(new RecursoNaoEncontradoException("Item do carrinho não encontrado"))
                .when(removerItemCarrinhoService).remover(999L);

        // Act & Assert
        mockMvc.perform(delete("/carrinho/itens/999")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("Item do carrinho não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar 401 ao tentar esvaziar carrinho sem autenticação")
    void deveRetornar401AoLimparCarrinhoSemAutenticacao() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/carrinho"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(limparCarrinhoService);
    }

    @Test
    @DisplayName("Deve esvaziar carrinho do comprador autenticado e retornar 204 No Content")
    void deveLimparCarrinhoComSucesso() throws Exception {
        // Arrange
        doNothing().when(limparCarrinhoService).limpar();

        // Act & Assert
        mockMvc.perform(delete("/carrinho")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_USUARIO"))))
                .andExpect(status().isNoContent());

        verify(limparCarrinhoService).limpar();
    }
}
