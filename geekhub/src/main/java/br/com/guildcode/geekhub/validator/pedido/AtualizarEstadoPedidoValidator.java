package br.com.guildcode.geekhub.validator.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.request.AtualizarEstadoPedidoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class AtualizarEstadoPedidoValidator {

    public void validarDadosAtualizacao(AtualizarEstadoPedidoRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados de atualização do pedido são obrigatórios");
        }

        if (isNull(request.estado())) {
            throw new NegocioException("O estado do pedido é obrigatório");
        }
    }
}
