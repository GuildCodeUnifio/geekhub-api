package br.com.guildcode.geekhub.repository.carrinho;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
}
