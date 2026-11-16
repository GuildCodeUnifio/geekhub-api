package br.com.guildcode.geekhub.validator.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class CadastrarAvaliacaoValidator extends BaseAvaliacaoValidator {

    public void validarDadosCadastro(CadastrarAvaliacaoRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados da avaliação são obrigatórios");
        }

        if (isNull(request.pedidoId())) {
            throw new NegocioException("O pedido da avaliação é obrigatório");
        }

        validarNota(request.nota());
    }
}
