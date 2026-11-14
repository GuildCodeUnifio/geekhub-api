package br.com.guildcode.geekhub.mapper.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.response.CadastrarUsuarioResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class CadastrarUsuarioMapper {

    public Usuario toEntity(CadastrarUsuarioRequest request) {

        return Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .imagemPerfil(request.imagemPerfil())
                .build();
    }

    public CadastrarUsuarioResponse toResponse(Usuario entity) {

        return CadastrarUsuarioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .imagemPerfil(entity.getImagemPerfil())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
