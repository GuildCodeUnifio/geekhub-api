package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import org.springframework.stereotype.Service;

@Service
public class BuscarItemCarrinhoService {

    private final ItemCarrinhoRepository itemCarrinhoRepository;

    public BuscarItemCarrinhoService(ItemCarrinhoRepository itemCarrinhoRepository) {

        this.itemCarrinhoRepository = itemCarrinhoRepository;
    }

    public ItemCarrinho buscarPorId(Long id) {

        return itemCarrinhoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item do carrinho não encontrado"));
    }
}
