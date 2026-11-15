package br.com.guildcode.geekhub.mapper.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.EditarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import org.springframework.stereotype.Component;

@Component
public class EditarCategoriaMapper {

    public EditarCategoriaResponse toResponse(Categoria entity) {

        return EditarCategoriaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .ativa(entity.getAtiva())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
