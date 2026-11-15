package br.com.guildcode.geekhub.repository.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("test")
class AvaliacaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Test
    @DisplayName("Deve calcular nota média e quantidade de avaliações apenas do vendedor informado")
    void deveCalcularNotaMediaEQuantidadeApenasDoVendedorInformado() {

        // Arrange
        Usuario comprador = persistirUsuario("Comprador", "comprador.aval@geekhub.com");
        Usuario vendedor = persistirUsuario("Vendedor Um", "vendedor1.aval@geekhub.com");
        Usuario outroVendedor = persistirUsuario("Vendedor Dois", "vendedor2.aval@geekhub.com");

        Pedido pedido1 = persistirPedido(comprador);
        Pedido pedido2 = persistirPedido(comprador);
        Pedido pedidoOutro = persistirPedido(comprador);

        persistirAvaliacao(pedido1, comprador, vendedor, 5);
        persistirAvaliacao(pedido2, comprador, vendedor, 4);
        persistirAvaliacao(pedidoOutro, comprador, outroVendedor, 2);

        entityManager.flush();
        entityManager.clear();

        // Act
        Double notaMedia = avaliacaoRepository.calcularNotaMediaPorVendedorId(vendedor.getId());
        Long quantidadeAvaliacoes = avaliacaoRepository.contarPorVendedorId(vendedor.getId());

        // Assert
        assertEquals(4.5, notaMedia);
        assertEquals(2L, quantidadeAvaliacoes);
    }

    @Test
    @DisplayName("Deve retornar nota média nula e quantidade zero quando vendedor não possuir avaliações")
    void deveRetornarNotaMediaNulaEQuantidadeZeroQuandoSemAvaliacoes() {

        // Arrange
        Usuario vendedorSemAvaliacoes = persistirUsuario("Vendedor Novo", "novo.aval@geekhub.com");

        entityManager.flush();
        entityManager.clear();

        // Act
        Double notaMedia = avaliacaoRepository.calcularNotaMediaPorVendedorId(vendedorSemAvaliacoes.getId());
        Long quantidadeAvaliacoes = avaliacaoRepository.contarPorVendedorId(vendedorSemAvaliacoes.getId());

        // Assert
        assertNull(notaMedia);
        assertEquals(0L, quantidadeAvaliacoes);
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

    private Pedido persistirPedido(Usuario comprador) {

        Pedido pedido = Pedido.builder()
                .comprador(comprador)
                .compradorNome(comprador.getNome())
                .compradorEmail(comprador.getEmail())
                .valorTotal(BigDecimal.valueOf(100.00))
                .estado(CONCLUIDO)
                .avaliado(TRUE)
                .criadoEm(LocalDateTime.of(2026, 1, 11, 10, 0))
                .build();

        return entityManager.persist(pedido);
    }

    private void persistirAvaliacao(Pedido pedido, Usuario autor, Usuario vendedor, Integer nota) {

        Avaliacao avaliacao = Avaliacao.builder()
                .pedido(pedido)
                .autor(autor)
                .vendedor(vendedor)
                .nota(nota)
                .comentario("Comentário de avaliação")
                .criadoEm(LocalDateTime.of(2026, 1, 12, 14, 0))
                .build();

        entityManager.persist(avaliacao);
    }
}
