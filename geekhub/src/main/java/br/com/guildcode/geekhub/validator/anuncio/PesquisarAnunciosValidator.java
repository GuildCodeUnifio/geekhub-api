package br.com.guildcode.geekhub.validator.anuncio;

import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.nonNull;

@Component
public class PesquisarAnunciosValidator {

    public void validarFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {

        if (nonNull(precoMin) && precoMin.compareTo(ZERO) < 0) {
            throw new NegocioException("Preço mínimo não pode ser negativo");
        }

        if (nonNull(precoMax) && precoMax.compareTo(ZERO) < 0) {
            throw new NegocioException("Preço máximo não pode ser negativo");
        }

        if (nonNull(precoMin) && nonNull(precoMax) && precoMin.compareTo(precoMax) > 0) {
            throw new NegocioException("Preço mínimo não pode ser maior que o preço máximo");
        }
    }
}
