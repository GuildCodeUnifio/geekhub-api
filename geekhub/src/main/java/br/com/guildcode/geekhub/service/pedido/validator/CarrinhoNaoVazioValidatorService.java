package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class CarrinhoNaoVazioValidatorService {

    public void validar(List<ItemCarrinho> itens) {

        if (isNull(itens) || itens.isEmpty()) {
            throw new NegocioException("O carrinho de compras está vazio");
        }
    }
}
