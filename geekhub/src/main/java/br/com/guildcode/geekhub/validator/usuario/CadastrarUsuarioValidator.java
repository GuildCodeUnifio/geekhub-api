package br.com.guildcode.geekhub.validator.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class CadastrarUsuarioValidator {

    public void validarDadosCadastro(CadastrarUsuarioRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados do usuário são obrigatórios");
        }
    }
}
