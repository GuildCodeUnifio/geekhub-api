package br.com.guildcode.geekhub.repository.pedido;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.domain.pedido.EventoPedido;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.NOVO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
class PedidoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Deve retornar true quando existir pedido contendo o anúncio informado")
    void deveRetornarTrueQuandoExistirPedidoParaOAnuncio() {

        // Arrange
        Usuario vendedor = persistirUsuario("Vendedor Teste", "vendedor.repo@email.com");
        Usuario comprador = persistirUsuario("Comprador Teste", "comprador.repo@email.com");
        Categoria categoria = persistirCategoria("Colecionáveis Repo");
        Anuncio anuncio = persistirAnuncio("Action Figure Goku", categoria, vendedor);
        persistirPedido(comprador, anuncio);
        entityManager.flush();
        entityManager.clear();

        // Act
        Boolean existePedido = pedidoRepository.existsByAnuncioId(anuncio.getId());

        // Assert
        assertTrue(existePedido);
    }

    @Test
    @DisplayName("Deve retornar false quando não existir pedido para o anúncio informado")
    void deveRetornarFalseQuandoNaoExistirPedidoParaOAnuncio() {

        // Arrange
        Usuario vendedor = persistirUsuario("Vendedor Teste 2", "vendedor2.repo@email.com");
        Usuario comprador = persistirUsuario("Comprador Teste 2", "comprador2.repo@email.com");
        Categoria categoria = persistirCategoria("Jogos Repo");
        Anuncio anuncioComPedido = persistirAnuncio("Cartucho SNES", categoria, vendedor);
        Anuncio anuncioSemPedido = persistirAnuncio("Controle Mega Drive", categoria, vendedor);
        persistirPedido(comprador, anuncioComPedido);
        entityManager.flush();
        entityManager.clear();

        // Act
        Boolean existePedido = pedidoRepository.existsByAnuncioId(anuncioSemPedido.getId());

        // Assert
        assertFalse(existePedido);
    }

    @Test
    @DisplayName("Deve listar pedidos distintos do vendedor ignorando pedidos de outros vendedores")
    void deveListarPedidosDistintosDoVendedor() {

        // Arrange
        Usuario vendedorAlvo = persistirUsuario("Vendedor Alvo", "vendedor.alvo@email.com");
        Usuario outroVendedor = persistirUsuario("Outro Vendedor", "vendedor.outro@email.com");
        Usuario comprador = persistirUsuario("Comprador Vendas", "comprador.vendas@email.com");
        Categoria categoria = persistirCategoria("Colecionáveis Vendas");

        Anuncio anuncio1 = persistirAnuncio("Item 1 Vendedor", categoria, vendedorAlvo);
        Anuncio anuncio2 = persistirAnuncio("Item 2 Vendedor", categoria, vendedorAlvo);
        Anuncio anuncioOutro = persistirAnuncio("Item Outro Vendedor", categoria, outroVendedor);

        Pedido pedidoComDoisItens = persistirPedidoComItens(comprador, List.of(anuncio1, anuncio2), CRIADO);
        persistirPedidoComItens(comprador, List.of(anuncioOutro), CRIADO);
        entityManager.flush();
        entityManager.clear();

        // Act
        Page<Pedido> vendas = pedidoRepository.listarVendas(vendedorAlvo.getId(), null, PageRequest.of(0, 10));

        // Assert
        assertEquals(1, vendas.getTotalElements());
        assertEquals(pedidoComDoisItens.getId(), vendas.getContent().getFirst().getId());
    }

    @Test
    @DisplayName("Deve filtrar vendas do vendedor pelo estado quando informado")
    void deveFiltrarVendasDoVendedorPorEstado() {

        // Arrange
        Usuario vendedor = persistirUsuario("Vendedor Filtro", "vendedor.filtro@email.com");
        Usuario comprador = persistirUsuario("Comprador Filtro", "comprador.filtro@email.com");
        Categoria categoria = persistirCategoria("Mangás Vendas");
        Anuncio anuncio = persistirAnuncio("Mangá Vendas", categoria, vendedor);

        persistirPedidoComItens(comprador, List.of(anuncio), CRIADO);
        Pedido pedidoConcluido = persistirPedidoComItens(comprador, List.of(anuncio), CONCLUIDO);
        entityManager.flush();
        entityManager.clear();

        // Act
        Page<Pedido> vendasConcluidas = pedidoRepository.listarVendas(
                vendedor.getId(), CONCLUIDO, PageRequest.of(0, 10));

        // Assert
        assertEquals(1, vendasConcluidas.getTotalElements());
        assertEquals(pedidoConcluido.getId(), vendasConcluidas.getContent().getFirst().getId());
    }

    @Test
    @DisplayName("Deve contar apenas pedidos distintos do vendedor no estado CONCLUIDO")
    void deveContarPedidosConcluidosDistintosPorVendedor() {

        // Arrange
        Usuario vendedorAlvo = persistirUsuario("Vendedor Contagem", "vendedor.cont@email.com");
        Usuario outroVendedor = persistirUsuario("Outro Vendedor Cont", "vendedor2.cont@email.com");
        Usuario comprador = persistirUsuario("Comprador Contagem", "comprador.cont@email.com");
        Categoria categoria = persistirCategoria("Games Contagem");

        Anuncio anuncio1 = persistirAnuncio("Item A", categoria, vendedorAlvo);
        Anuncio anuncio2 = persistirAnuncio("Item B", categoria, vendedorAlvo);
        Anuncio anuncioOutro = persistirAnuncio("Item C", categoria, outroVendedor);

        persistirPedidoComItens(comprador, List.of(anuncio1, anuncio2), CONCLUIDO);
        persistirPedidoComItens(comprador, List.of(anuncio1), CRIADO);
        persistirPedidoComItens(comprador, List.of(anuncioOutro), CONCLUIDO);
        entityManager.flush();
        entityManager.clear();

        // Act
        Long quantidadeVendas = pedidoRepository.contarPorVendedorIdEEstado(vendedorAlvo.getId(), CONCLUIDO);

        // Assert
        assertEquals(1L, quantidadeVendas);
    }

    private Usuario persistirUsuario(String nome, String email) {

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha("$2a$10$VrIbJURwINOR5HOrWFFTNOwSILsioRJSuOGAg8Luvr9qZDSOl5JXG")
                .imagemPerfil("https://picsum.photos/200")
                .ativo(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        return entityManager.persistFlushFind(usuario);
    }

    private Categoria persistirCategoria(String nome) {

        Categoria categoria = Categoria.builder()
                .nome(nome)
                .descricao("Categoria para teste de repositório")
                .ativa(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        return entityManager.persistFlushFind(categoria);
    }

    private Anuncio persistirAnuncio(String nome, Categoria categoria, Usuario vendedor) {

        Anuncio anuncio = Anuncio.builder()
                .nome(nome)
                .descricao("Descrição do produto para teste de repositório")
                .preco(new BigDecimal("199.90"))
                .quantidadeDisponivel(2)
                .disponivel(TRUE)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .categoria(categoria)
                .vendedor(vendedor)
                .imagens(List.of("https://picsum.photos/200"))
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        return entityManager.persistFlushFind(anuncio);
    }

    private void persistirPedido(Usuario comprador, Anuncio anuncio) {

        persistirPedidoComItens(comprador, List.of(anuncio), CRIADO);
    }

    private Pedido persistirPedidoComItens(Usuario comprador, List<Anuncio> anuncios, EstadoPedido estado) {

        LocalDateTime criadoEm = LocalDateTime.of(2026, 1, 10, 12, 30);

        List<ItemPedido> itens = anuncios.stream()
                .map(anuncio -> ItemPedido.builder()
                        .anuncioId(anuncio.getId())
                        .vendedorId(anuncio.getVendedor().getId())
                        .nomeProduto(anuncio.getNome())
                        .imagemProduto(anuncio.getImagens().getFirst())
                        .precoUnitario(anuncio.getPreco())
                        .quantidade(1)
                        .subtotal(anuncio.getPreco())
                        .build())
                .toList();

        BigDecimal valorTotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        EventoPedido evento = EventoPedido.builder()
                .estado(estado)
                .ocorridoEm(criadoEm)
                .build();

        Pedido pedido = Pedido.builder()
                .comprador(comprador)
                .compradorNome(comprador.getNome())
                .compradorEmail(comprador.getEmail())
                .valorTotal(valorTotal)
                .estado(estado)
                .avaliado(FALSE)
                .itens(itens)
                .historico(List.of(evento))
                .criadoEm(criadoEm)
                .build();

        return entityManager.persistFlushFind(pedido);
    }
}
