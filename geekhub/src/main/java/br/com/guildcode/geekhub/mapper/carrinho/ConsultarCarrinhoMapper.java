package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ConsultarCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ItemCarrinhoDetalheResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ConsultarCarrinhoMapper {

    public ItemCarrinhoDetalheResponse toItemResponse(ItemCarrinho entity) {

        Anuncio anuncio = entity.getAnuncio();

        return ItemCarrinhoDetalheResponse.builder()
                .id(entity.getId())
                .anuncioId(anuncio.getId())
                .vendedorId(anuncio.getVendedor().getId())
                .nome(anuncio.getNome())
                .imagem(anuncio.obterImagemPrincipal())
                .precoUnitario(anuncio.getPreco())
                .quantidade(entity.getQuantidade())
                .quantidadeDisponivel(anuncio.getQuantidadeDisponivel())
                .disponivel(anuncio.getDisponivel())
                .subtotal(entity.calcularSubtotal())
                .build();
    }

    public ConsultarCarrinhoResponse toResponse(
            Long compradorId,
            List<ItemCarrinhoDetalheResponse> itens,
            BigDecimal total) {

        return ConsultarCarrinhoResponse.builder()
                .compradorId(compradorId)
                .itens(itens)
                .total(total)
                .build();
    }
}
