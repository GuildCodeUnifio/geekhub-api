package br.com.guildcode.geekhub.validator.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class AdicionarItemCarrinhoValidator {

    public void validarDadosAdicao(AdicionarItemCarrinhoRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados do item do carrinho são obrigatórios");
        }
    }
}
