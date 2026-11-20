package br.com.guildcode.geekhub.repository.pedido;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Long countByEstado(EstadoPedido estado);

    List<Pedido> findTop5ByOrderByCriadoEmDesc();

    @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.estado <> :estadoExcluido")
    BigDecimal somarValorTotalExcluindoEstado(EstadoPedido estadoExcluido);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p JOIN p.itens item WHERE item.anuncioId = :anuncioId")
    Boolean existsByAnuncioId(Long anuncioId);

    Page<Pedido> findByCompradorId(Long compradorId, Pageable pageable);

    Page<Pedido> findByCompradorIdAndEstado(Long compradorId, EstadoPedido estado, Pageable pageable);

    @Query("""
            SELECT DISTINCT p FROM Pedido p
            JOIN p.itens item
            WHERE item.vendedorId = :vendedorId
              AND (:estado IS NULL OR p.estado = :estado)
            """)
    Page<Pedido> listarVendas(Long vendedorId, EstadoPedido estado, Pageable pageable);

    @Query("""
            SELECT COUNT(DISTINCT p) FROM Pedido p
            JOIN p.itens item
            WHERE item.vendedorId = :vendedorId
              AND p.estado = :estado
            """)
    Long contarPorVendedorIdEEstado(Long vendedorId, EstadoPedido estado);
}
