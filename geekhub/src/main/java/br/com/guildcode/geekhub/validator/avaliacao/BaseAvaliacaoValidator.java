package br.com.guildcode.geekhub.validator.avaliacao;

import br.com.guildcode.geekhub.exception.NegocioException;

import static java.util.Objects.isNull;

abstract class BaseAvaliacaoValidator {

    protected void validarNota(Integer nota) {

        if (isNull(nota) || nota < 1 || nota > 5) {
            throw new NegocioException("A nota da avaliação deve ser entre 1 e 5");
        }
    }
}
