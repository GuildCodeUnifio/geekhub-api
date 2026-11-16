package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.response.AtualizarPermissaoUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AtualizarPermissaoUsuarioMapper {

    public AtualizarPermissaoUsuarioResponse toResponse(Usuario entity) {

        return AtualizarPermissaoUsuarioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .admin(entity.isAdmin())
                .permissoes(entity.obterNomesPermissoes())
                .build();
    }
}
