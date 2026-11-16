package br.com.guildcode.geekhub.mapper.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.response.EditarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import org.springframework.stereotype.Component;

@Component
public class EditarAvaliacaoMapper {

    public EditarAvaliacaoResponse toResponse(Avaliacao entity) {

        return EditarAvaliacaoResponse.builder()
                .id(entity.getId())
                .pedidoId(entity.getPedido().getId())
                .vendedorId(entity.getVendedor().getId())
                .autorId(entity.getAutor().getId())
                .autorNome(entity.getAutor().getNome())
                .nota(entity.getNota())
                .comentario(entity.getComentario())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
