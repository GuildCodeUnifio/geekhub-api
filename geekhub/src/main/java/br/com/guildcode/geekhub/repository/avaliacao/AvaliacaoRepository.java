package br.com.guildcode.geekhub.repository.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Boolean existsByPedidoId(Long pedidoId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.vendedor.id = :vendedorId")
    Double calcularNotaMediaPorVendedorId(Long vendedorId);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.vendedor.id = :vendedorId")
    Long contarPorVendedorId(Long vendedorId);
}
