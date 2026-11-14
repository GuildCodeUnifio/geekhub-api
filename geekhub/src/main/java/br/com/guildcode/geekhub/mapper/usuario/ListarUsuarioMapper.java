package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.response.ListarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class ListarUsuarioMapper {

    public ListarUsuarioResponse toResponse(Usuario entity) {

        return ListarUsuarioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .imagemPerfil(entity.getImagemPerfil())
                .ativo(entity.getAtivo())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
