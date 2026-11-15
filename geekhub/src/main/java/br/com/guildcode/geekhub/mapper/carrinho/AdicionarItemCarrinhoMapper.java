package br.com.guildcode.geekhub.mapper.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.AdicionarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdicionarItemCarrinhoMapper {

    public ItemCarrinho toEntity(
            Usuario comprador,
            Anuncio anuncio,
            Integer quantidade,
            LocalDateTime criadoEm) {

        return ItemCarrinho.builder()
                .comprador(comprador)
                .anuncio(anuncio)
                .quantidade(quantidade)
                .criadoEm(criadoEm)
                .build();
    }

    public AdicionarItemCarrinhoResponse toResponse(ItemCarrinho entity) {

        Anuncio anuncio = entity.getAnuncio();

        return AdicionarItemCarrinhoResponse.builder()
                .id(entity.getId())
                .anuncioId(anuncio.getId())
                .nomeProduto(anuncio.getNome())
                .precoUnitario(anuncio.getPreco())
                .quantidade(entity.getQuantidade())
                .subtotal(entity.calcularSubtotal())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
