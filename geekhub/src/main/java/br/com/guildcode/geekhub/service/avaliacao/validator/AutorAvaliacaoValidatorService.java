package br.com.guildcode.geekhub.service.avaliacao.validator;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AutorAvaliacaoValidatorService {

    public void validarAutor(Avaliacao avaliacao, Usuario usuarioAutenticado) {

        if (!avaliacao.pertenceAoAutor(usuarioAutenticado.getId())) {
            throw new AccessDeniedException("Você não possui permissão para editar esta avaliação");
        }
    }

    public void validarAutorOuAdmin(Avaliacao avaliacao, Usuario usuarioAutenticado) {

        Boolean isAutor = avaliacao.pertenceAoAutor(usuarioAutenticado.getId());
        Boolean isAdmin = usuarioAutenticado.isAdmin();

        if (!isAutor && !isAdmin) {
            throw new AccessDeniedException("Você não possui permissão para remover esta avaliação");
        }
    }
}
