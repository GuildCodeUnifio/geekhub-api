package br.com.guildcode.geekhub.validator.anuncio;

import br.com.guildcode.geekhub.exception.NegocioException;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.isNull;

abstract class BaseAnuncioValidator {

    protected void validarCamposComuns(BigDecimal preco, List<String> imagens) {

        if (isNull(preco) || preco.compareTo(ZERO) <= 0) {
            throw new NegocioException("Preço deve ser maior que zero");
        }

        if (isNull(imagens) || imagens.isEmpty()) {
            throw new NegocioException("Anúncio deve possuir ao menos uma imagem");
        }
    }
}
