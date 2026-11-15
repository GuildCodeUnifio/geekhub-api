package br.com.guildcode.geekhub.repository.pedido;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
