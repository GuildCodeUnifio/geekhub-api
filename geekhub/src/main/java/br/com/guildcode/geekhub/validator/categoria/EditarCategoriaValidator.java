package br.com.guildcode.geekhub.validator.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class EditarCategoriaValidator {

    public void validarDadosEdicao(EditarCategoriaRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados da categoria são obrigatórios");
        }
    }
}
