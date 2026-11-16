package br.com.guildcode.geekhub.repository.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
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
import static br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto.USADO;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.BOM;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.EXCELENTE;
import static br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto.REGULAR;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
class AnuncioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Test
    @DisplayName("Deve listar no catálogo apenas anúncios disponíveis e com estoque maior que zero")
    void deveListarApenasAnunciosDisponiveisComEstoque() {

        // Arrange
        Categoria categoria = persistirCategoria("Figuras");
        Usuario vendedor = persistirUsuario("Vendedor Um", "v1.anuncio@geekhub.com");

        Anuncio disponivel = persistirAnuncio(
                "Action Figure Naruto", BigDecimal.valueOf(250.00), 3, TRUE, NOVO, EXCELENTE, categoria, vendedor);
        persistirAnuncio(
                "Action Figure Indisponível", BigDecimal.valueOf(250.00), 3, FALSE, NOVO, EXCELENTE, categoria, vendedor);
        persistirAnuncio(
                "Action Figure Sem Estoque", BigDecimal.valueOf(250.00), 0, TRUE, NOVO, EXCELENTE, categoria, vendedor);

        entityManager.flush();
        entityManager.clear();

        // Act
        Page<Anuncio> pagina = anuncioRepository.pesquisarCatalogo(
                null, null, null, null, null, null, null, PageRequest.of(0, 10));

        // Assert
        assertEquals(1, pagina.getTotalElements());
        assertEquals(disponivel.getId(), pagina.getContent().getFirst().getId());
    }

    @Test
    @DisplayName("Deve pesquisar catálogo por nome parcial ignorando maiúsculas e minúsculas")
    void devePesquisarCatalogoPorNomeCaseInsensitive() {

        // Arrange
        Categoria categoria = persistirCategoria("Mangás");
        Usuario vendedor = persistirUsuario("Vendedor Dois", "v2.anuncio@geekhub.com");

        Anuncio naruto = persistirAnuncio(
                "Action figure Naruto 1ª edição", BigDecimal.valueOf(190.00), 2, TRUE, NOVO, EXCELENTE, categoria, vendedor);
        persistirAnuncio(
                "Mangá One Piece", BigDecimal.valueOf(45.00), 5, TRUE, NOVO, EXCELENTE, categoria, vendedor);

        entityManager.flush();
        entityManager.clear();

        // Act
        Page<Anuncio> pagina = anuncioRepository.pesquisarCatalogo(
                "nArUtO", null, null, null, null, null, null, PageRequest.of(0, 10));

        // Assert
        assertEquals(1, pagina.getTotalElements());
        assertEquals(naruto.getId(), pagina.getContent().getFirst().getId());
    }

    @Test
    @DisplayName("Deve combinar múltiplos filtros no catálogo com operador AND")
    void deveCombinarMultiplosFiltrosComAnd() {

        // Arrange
        Categoria figuras = persistirCategoria("Figuras");
        Categoria jogos = persistirCategoria("Jogos");
        Usuario vendedor1 = persistirUsuario("Vendedor Um", "v3a.anuncio@geekhub.com");
        Usuario vendedor2 = persistirUsuario("Vendedor Dois", "v3b.anuncio@geekhub.com");

        Anuncio alvo = persistirAnuncio(
                "Estátua Kakashi", BigDecimal.valueOf(300.00), 2, TRUE, USADO, BOM, figuras, vendedor1);
        persistirAnuncio("Estátua Kakashi Barata", BigDecimal.valueOf(100.00), 2, TRUE, USADO, BOM, figuras, vendedor1);
        persistirAnuncio("Estátua Kakashi Cara", BigDecimal.valueOf(500.00), 2, TRUE, USADO, BOM, figuras, vendedor1);
        persistirAnuncio("Estátua Kakashi Outro Cat", BigDecimal.valueOf(300.00), 2, TRUE, USADO, BOM, jogos, vendedor1);
        persistirAnuncio("Estátua Kakashi Novo", BigDecimal.valueOf(300.00), 2, TRUE, NOVO, BOM, figuras, vendedor1);
        persistirAnuncio("Estátua Kakashi Regular", BigDecimal.valueOf(300.00), 2, TRUE, USADO, REGULAR, figuras, vendedor1);
        persistirAnuncio("Estátua Kakashi Outro Vend", BigDecimal.valueOf(300.00), 2, TRUE, USADO, BOM, figuras, vendedor2);

        entityManager.flush();
        entityManager.clear();

        // Act
        Page<Anuncio> pagina = anuncioRepository.pesquisarCatalogo(
                "kakashi",
                figuras.getId(),
                BigDecimal.valueOf(200.00),
                BigDecimal.valueOf(400.00),
                USADO,
                BOM,
                vendedor1.getId(),
                PageRequest.of(0, 10));

        // Assert
        assertEquals(1, pagina.getTotalElements());
        assertEquals(alvo.getId(), pagina.getContent().getFirst().getId());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando nenhum anúncio corresponder aos filtros")
    void deveRetornarPaginaVaziaQuandoSemResultados() {

        // Arrange
        Categoria categoria = persistirCategoria("Quadrinhos");
        Usuario vendedor = persistirUsuario("Vendedor Quatro", "v4.anuncio@geekhub.com");
        persistirAnuncio("HQ Batman", BigDecimal.valueOf(120.00), 1, TRUE, USADO, BOM, categoria, vendedor);

        entityManager.flush();
        entityManager.clear();

        // Act
        Page<Anuncio> pagina = anuncioRepository.pesquisarCatalogo(
                "Inexistente", null, null, null, null, null, null, PageRequest.of(0, 10));

        // Assert
        assertTrue(pagina.isEmpty());
        assertEquals(0, pagina.getTotalElements());
    }

    @Test
    @DisplayName("Deve listar e contar apenas anúncios disponíveis com estoque maior que zero do vendedor em ordem decrescente")
    void deveListarEContarAnunciosDisponiveisComEstoquePorVendedor() {

        // Arrange
        Categoria categoria = persistirCategoria("Colecionáveis Vendedor");
        Usuario vendedor = persistirUsuario("Vendedor Alvo", "v4a.anuncio@geekhub.com");
        Usuario outroVendedor = persistirUsuario("Outro Vendedor", "v4b.anuncio@geekhub.com");

        Anuncio antigo = Anuncio.builder()
                .nome("Anúncio Antigo")
                .descricao("Descrição antigo")
                .preco(BigDecimal.valueOf(120.00))
                .quantidadeDisponivel(2)
                .disponivel(TRUE)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .categoria(categoria)
                .vendedor(vendedor)
                .imagens(List.of("https://picsum.photos/200"))
                .criadoEm(LocalDateTime.of(2026, 1, 10, 10, 0))
                .build();
        Anuncio recente = Anuncio.builder()
                .nome("Anúncio Recente")
                .descricao("Descrição recente")
                .preco(BigDecimal.valueOf(220.00))
                .quantidadeDisponivel(5)
                .disponivel(TRUE)
                .condicao(NOVO)
                .conservacao(EXCELENTE)
                .categoria(categoria)
                .vendedor(vendedor)
                .imagens(List.of("https://picsum.photos/200"))
                .criadoEm(LocalDateTime.of(2026, 1, 12, 10, 0))
                .build();
        entityManager.persist(antigo);
        entityManager.persist(recente);

        persistirAnuncio("Indisponível", BigDecimal.valueOf(100.00), 3, FALSE, NOVO, EXCELENTE, categoria, vendedor);
        persistirAnuncio("Sem Estoque", BigDecimal.valueOf(100.00), 0, TRUE, NOVO, EXCELENTE, categoria, vendedor);
        persistirAnuncio("Outro Vendedor", BigDecimal.valueOf(100.00), 3, TRUE, NOVO, EXCELENTE, categoria, outroVendedor);

        entityManager.flush();
        entityManager.clear();

        // Act
        List<Anuncio> disponiveis = anuncioRepository.listarDisponiveisPorVendedorId(vendedor.getId());
        Long quantidade = anuncioRepository.contarDisponiveisPorVendedorId(vendedor.getId());

        // Assert
        assertEquals(2L, quantidade);
        assertEquals(2, disponiveis.size());
        assertEquals(recente.getId(), disponiveis.getFirst().getId());
        assertEquals(antigo.getId(), disponiveis.getLast().getId());
    }

    private Categoria persistirCategoria(String nome) {

        Categoria categoria = Categoria.builder()
                .nome(nome)
                .descricao("Descrição " + nome)
                .ativa(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        return entityManager.persist(categoria);
    }

    private Usuario persistirUsuario(String nome, String email) {

        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(email)
                .senha("$2a$10$hash")
                .ativo(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 10, 12, 0))
                .build();

        return entityManager.persist(usuario);
    }

    private Anuncio persistirAnuncio(
            String nome,
            BigDecimal preco,
            Integer quantidade,
            Boolean disponivel,
            CondicaoProduto condicao,
            ConservacaoProduto conservacao,
            Categoria categoria,
            Usuario vendedor) {

        Anuncio anuncio = Anuncio.builder()
                .nome(nome)
                .descricao("Descrição detalhada de " + nome)
                .preco(preco)
                .quantidadeDisponivel(quantidade)
                .disponivel(disponivel)
                .condicao(condicao)
                .conservacao(conservacao)
                .categoria(categoria)
                .vendedor(vendedor)
                .imagens(List.of("https://picsum.photos/200"))
                .criadoEm(LocalDateTime.of(2026, 1, 11, 10, 0))
                .build();

        return entityManager.persist(anuncio);
    }
}
