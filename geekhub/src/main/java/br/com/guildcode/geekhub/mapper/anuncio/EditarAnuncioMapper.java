package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class EditarAnuncioMapper {

    public EditarAnuncioResponse toResponse(Anuncio entity) {

        return EditarAnuncioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .categoriaId(entity.getCategoria().getId())
                .categoriaNome(entity.getCategoria().getNome())
                .preco(entity.getPreco())
                .quantidadeDisponivel(entity.getQuantidadeDisponivel())
                .disponivel(entity.getDisponivel())
                .condicao(entity.getCondicao())
                .conservacao(entity.getConservacao())
                .imagens(new ArrayList<>(entity.getImagens()))
                .vendedorId(entity.getVendedor().getId())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
