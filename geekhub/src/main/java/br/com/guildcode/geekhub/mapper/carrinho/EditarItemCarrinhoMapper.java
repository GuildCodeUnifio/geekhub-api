package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.EditarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import org.springframework.stereotype.Component;

@Component
public class EditarItemCarrinhoMapper {

    public EditarItemCarrinhoResponse toResponse(ItemCarrinho entity) {

        Anuncio anuncio = entity.getAnuncio();

        return EditarItemCarrinhoResponse.builder()
                .id(entity.getId())
                .anuncioId(anuncio.getId())
                .nomeProduto(anuncio.getNome())
                .precoUnitario(anuncio.getPreco())
                .quantidade(entity.getQuantidade())
                .subtotal(entity.calcularSubtotal())
                .build();
    }
}
