package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.exception.ConflitoException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Boolean.TRUE;

@Service
public class EstoqueDisponivelPedidoValidatorService {

    public void validar(List<ItemCarrinho> itens) {

        for (ItemCarrinho item : itens) {
            Anuncio anuncio = item.getAnuncio();

            if (!TRUE.equals(anuncio.getDisponivel()) || anuncio.getQuantidadeDisponivel() <= 0) {
                throw new ConflitoException(
                        "O anúncio '" + anuncio.getNome() + "' não está mais disponível para compra");
            }

            if (item.getQuantidade() > anuncio.getQuantidadeDisponivel()) {
                throw new ConflitoException(
                        "Estoque insuficiente para o anúncio '" + anuncio.getNome() + "'");
            }
        }
    }
}
