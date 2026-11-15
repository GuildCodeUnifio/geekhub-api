package br.com.guildcode.geekhub.service.carrinho.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class ValidarDisponibilidadeItemValidatorService {

    public void validar(Anuncio anuncio, Usuario comprador, Integer quantidadeTotal) {

        if (!TRUE.equals(anuncio.getDisponivel()) || anuncio.getQuantidadeDisponivel() <= 0) {
            throw new NegocioException("Anúncio indisponível ou sem estoque para compra");
        }

        if (anuncio.getVendedor().getId().equals(comprador.getId())) {
            throw new NegocioException("Você não pode adicionar o próprio anúncio ao carrinho");
        }

        if (quantidadeTotal > anuncio.getQuantidadeDisponivel()) {
            throw new NegocioException("Quantidade solicitada excede o estoque disponível do anúncio");
        }
    }
}
