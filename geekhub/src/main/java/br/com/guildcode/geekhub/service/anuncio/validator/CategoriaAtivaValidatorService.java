package br.com.guildcode.geekhub.service.anuncio.validator;

import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class CategoriaAtivaValidatorService {

    public void validar(Categoria categoria) {

        if (!TRUE.equals(categoria.getAtiva())) {

            throw new NegocioException("Não é possível vincular anúncio a uma categoria desativada");
        }
    }
}
