package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class CadastrarAnuncioMapper {

    public Anuncio toEntity(CadastrarAnuncioRequest request) {

        return Anuncio.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .preco(request.preco())
                .quantidadeDisponivel(request.quantidadeDisponivel())
                .condicao(request.condicao())
                .conservacao(request.conservacao())
                .imagens(new ArrayList<>(request.imagens()))
                .build();
    }

    public CadastrarAnuncioResponse toResponse(Anuncio entity) {

        return CadastrarAnuncioResponse.builder()
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
