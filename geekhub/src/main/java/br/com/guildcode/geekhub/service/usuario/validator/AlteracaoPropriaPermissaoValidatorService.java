package br.com.guildcode.geekhub.service.usuario.validator;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Service;

@Service
public class AlteracaoPropriaPermissaoValidatorService {

    public void validar(Usuario usuarioAutenticado, Usuario usuarioAlvo) {

        if (usuarioAutenticado.getId().equals(usuarioAlvo.getId())) {
            throw new NegocioException("Não é possível alterar a própria permissão de administrador");
        }
    }
}
