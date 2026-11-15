package br.com.guildcode.geekhub.repository.pedido;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pedido p JOIN p.itens item WHERE item.anuncioId = :anuncioId")
    Boolean existsByAnuncioId(Long anuncioId);
}
