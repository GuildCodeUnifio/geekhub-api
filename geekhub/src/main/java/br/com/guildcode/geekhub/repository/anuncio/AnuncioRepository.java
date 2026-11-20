package br.com.guildcode.geekhub.repository.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    Boolean existsByCategoriaId(Long categoriaId);

    Page<Anuncio> findByVendedorId(Long vendedorId, Pageable pageable);

    @Query("""
            SELECT a FROM Anuncio a
            WHERE a.vendedor.id = :vendedorId
              AND a.disponivel = true
              AND a.quantidadeDisponivel > 0
            ORDER BY a.criadoEm DESC
            """)
    List<Anuncio> listarDisponiveisPorVendedorId(Long vendedorId);

    @Query("""
            SELECT COUNT(a) FROM Anuncio a
            WHERE a.vendedor.id = :vendedorId
              AND a.disponivel = true
              AND a.quantidadeDisponivel > 0
            """)
    Long contarDisponiveisPorVendedorId(Long vendedorId);

    @Query("""
            SELECT COUNT(a) FROM Anuncio a
            WHERE a.disponivel = true
              AND a.quantidadeDisponivel > 0
            """)
    Long contarDisponiveis();

    @Query("SELECT SUM(a.quantidadeDisponivel) FROM Anuncio a")
    Long somarUnidadesEmEstoque();

    @Query("""
            SELECT a.categoria.nome AS categoriaNome,
                   COUNT(a) AS quantidadeAnuncios,
                   SUM(a.quantidadeDisponivel) AS estoqueTotal
            FROM Anuncio a
            GROUP BY a.categoria.id, a.categoria.nome
            ORDER BY COUNT(a) DESC, a.categoria.nome ASC
            """)
    List<ResumoCategoriaAnuncioProjection> agruparPorCategoria();

    @Query("""
            SELECT a FROM Anuncio a
            WHERE a.disponivel = true
              AND a.quantidadeDisponivel > 0
              AND (:nome IS NULL OR LOWER(a.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:categoriaId IS NULL OR a.categoria.id = :categoriaId)
              AND (:precoMin IS NULL OR a.preco >= :precoMin)
              AND (:precoMax IS NULL OR a.preco <= :precoMax)
              AND (:condicao IS NULL OR a.condicao = :condicao)
              AND (:conservacao IS NULL OR a.conservacao = :conservacao)
              AND (:vendedorId IS NULL OR a.vendedor.id = :vendedorId)
            """)
    Page<Anuncio> pesquisarCatalogo(
            String nome,
            Long categoriaId,
            BigDecimal precoMin,
            BigDecimal precoMax,
            CondicaoProduto condicao,
            ConservacaoProduto conservacao,
            Long vendedorId,
            Pageable pageable);
}
