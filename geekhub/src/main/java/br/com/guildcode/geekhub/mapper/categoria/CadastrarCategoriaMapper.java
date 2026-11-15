package br.com.guildcode.geekhub.mapper.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.CadastrarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CadastrarCategoriaMapper {

    public Categoria toEntity(CadastrarCategoriaRequest request) {

        return Categoria.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .build();
    }

    public CadastrarCategoriaResponse toResponse(Categoria entity) {

        return CadastrarCategoriaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .descricao(entity.getDescricao())
                .ativa(entity.getAtiva())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
