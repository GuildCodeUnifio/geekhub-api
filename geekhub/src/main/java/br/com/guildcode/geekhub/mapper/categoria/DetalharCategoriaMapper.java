package br.com.guildcode.geekhub.mapper.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.DetalharCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import org.springframework.stereotype.Component;

@Component
public class DetalharCategoriaMapper {

    public DetalharCategoriaResponse toResponse(Categoria entity) {

        return DetalharCategoriaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .ativa(entity.getAtiva())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
