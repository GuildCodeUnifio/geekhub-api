package br.com.guildcode.geekhub.service.usuario.validator;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AcessoUsuarioValidatorService {

    public void validarVisualizacao(Long usuarioAlvoId, Usuario usuarioAutenticado) {

        if (!isProprioUsuarioOuAdmin(usuarioAlvoId, usuarioAutenticado)) {
            throw new AccessDeniedException("Você não possui permissão para visualizar este usuário");
        }
    }

    public void validarEdicao(Long usuarioAlvoId, Usuario usuarioAutenticado) {

        if (!isProprioUsuarioOuAdmin(usuarioAlvoId, usuarioAutenticado)) {
            throw new AccessDeniedException("Você não possui permissão para editar este usuário");
        }
    }

    private Boolean isProprioUsuarioOuAdmin(Long usuarioAlvoId, Usuario usuarioAutenticado) {

        return usuarioAlvoId.equals(usuarioAutenticado.getId())
                || usuarioAutenticado.isAdmin();
    }
}
