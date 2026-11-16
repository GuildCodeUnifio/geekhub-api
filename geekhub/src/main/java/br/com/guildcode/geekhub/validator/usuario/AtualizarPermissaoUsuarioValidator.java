package br.com.guildcode.geekhub.validator.usuario;

import br.com.guildcode.geekhub.controller.usuario.dto.request.AtualizarPermissaoUsuarioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class AtualizarPermissaoUsuarioValidator {

    public void validarDadosAtualizacao(AtualizarPermissaoUsuarioRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados de atualização de permissão são obrigatórios");
        }

        if (isNull(request.admin())) {
            throw new NegocioException("O indicador de administrador é obrigatório");
        }
    }
}
