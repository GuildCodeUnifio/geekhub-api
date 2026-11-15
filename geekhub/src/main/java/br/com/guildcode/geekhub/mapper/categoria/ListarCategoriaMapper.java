package br.com.guildcode.geekhub.mapper.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.ListarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import org.springframework.stereotype.Component;

@Component
public class ListarCategoriaMapper {

    public ListarCategoriaResponse toResponse(Categoria entity) {

        return ListarCategoriaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .ativa(entity.getAtiva())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
