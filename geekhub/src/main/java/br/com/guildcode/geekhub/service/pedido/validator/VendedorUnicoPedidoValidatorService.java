package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.exception.ConflitoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendedorUnicoPedidoValidatorService {

    public void validar(List<ItemCarrinho> itens) {

        Long quantidadeVendedores = itens.stream()
                .map(item -> item.getAnuncio().getVendedor().getId())
                .distinct()
                .count();

        if (quantidadeVendedores > 1L) {
            throw new ConflitoException("Todos os itens do pedido devem pertencer ao mesmo vendedor");
        }
    }
}
