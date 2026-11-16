package br.com.guildcode.geekhub.validator.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class EditarAvaliacaoValidator extends BaseAvaliacaoValidator {

    public void validarDadosEdicao(EditarAvaliacaoRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados da avaliação são obrigatórios");
        }

        validarNota(request.nota());
    }
}
