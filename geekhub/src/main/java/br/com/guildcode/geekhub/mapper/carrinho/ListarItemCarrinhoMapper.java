package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ListarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import org.springframework.stereotype.Component;

@Component
public class ListarItemCarrinhoMapper {

    public ListarItemCarrinhoResponse toResponse(ItemCarrinho entity) {

        Anuncio anuncio = entity.getAnuncio();

        return ListarItemCarrinhoResponse.builder()
                .id(entity.getId())
                .anuncioId(anuncio.getId())
                .vendedorId(anuncio.getVendedor().getId())
                .nomeProduto(anuncio.getNome())
                .imagem(anuncio.obterImagemPrincipal())
                .precoUnitario(anuncio.getPreco())
                .quantidade(entity.getQuantidade())
                .quantidadeDisponivel(anuncio.getQuantidadeDisponivel())
                .disponivel(anuncio.getDisponivel())
                .subtotal(entity.calcularSubtotal())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
