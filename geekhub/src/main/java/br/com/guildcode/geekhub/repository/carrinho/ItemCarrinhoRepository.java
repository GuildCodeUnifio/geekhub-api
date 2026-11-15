package br.com.guildcode.geekhub.repository.carrinho;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {

    Optional<ItemCarrinho> findByCompradorIdAndAnuncioId(Long compradorId, Long anuncioId);

    List<ItemCarrinho> findByCompradorIdOrderByCriadoEmDesc(Long compradorId);

    Page<ItemCarrinho> findByCompradorId(Long compradorId, Pageable pageable);
}
