package br.com.guildcode.geekhub.validator.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class EditarAnuncioValidator extends BaseAnuncioValidator {

    public void validarDadosEdicao(EditarAnuncioRequest request) {

        if (isNull(request)) {
            throw new NegocioException("Dados do anúncio são obrigatórios");
        }

        validarCamposComuns(request.preco(), request.imagens());

        if (isNull(request.quantidadeDisponivel()) || request.quantidadeDisponivel() < 0) {
            throw new NegocioException("Quantidade disponível não pode ser negativa");
        }

        if (isNull(request.disponivel())) {
            throw new NegocioException("Disponibilidade do anúncio é obrigatória");
        }
    }
}
