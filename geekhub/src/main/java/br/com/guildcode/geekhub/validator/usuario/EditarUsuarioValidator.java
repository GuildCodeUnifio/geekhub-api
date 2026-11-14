package br.com.guildcode.geekhub.validator.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.EditarUsuarioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class EditarUsuarioValidator {

    public void validarDadosEdicao(EditarUsuarioRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados do usuário são obrigatórios");
        }
    }
}
