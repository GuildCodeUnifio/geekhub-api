package br.com.guildcode.geekhub.validator.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.EditarItemCarrinhoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class EditarItemCarrinhoValidator {

    public void validarDadosEdicao(EditarItemCarrinhoRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados de edição do item do carrinho são obrigatórios");
        }

        if (isNull(request.quantidade()) || request.quantidade() <= 0) {
            throw new NegocioException("A quantidade deve ser maior que zero");
        }
    }
}
