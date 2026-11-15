package br.com.guildcode.geekhub.mapper.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioAdminResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.stereotype.Component;

@Component
public class ListarAnuncioAdminMapper {

    public ListarAnuncioAdminResponse toResponse(Anuncio entity) {

        return ListarAnuncioAdminResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .preco(entity.getPreco())
                .quantidadeDisponivel(entity.getQuantidadeDisponivel())
                .disponivel(entity.getDisponivel())
                .categoriaNome(entity.getCategoria().getNome())
                .vendedorId(entity.getVendedor().getId())
                .vendedorNome(entity.getVendedor().getNome())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
