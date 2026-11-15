package br.com.guildcode.geekhub.service.anuncio.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ProprietarioAnuncioValidatorService {

    public void validarProprietario(Anuncio anuncio, Usuario usuarioAutenticado) {

        if (!anuncio.getVendedor().getId().equals(usuarioAutenticado.getId())) {

            throw new AccessDeniedException("Você não possui permissão para alterar este anúncio");
        }
    }

    public void validarProprietarioOuAdmin(Anuncio anuncio, Usuario usuarioAutenticado) {

        Boolean ehProprietario = anuncio.getVendedor().getId().equals(usuarioAutenticado.getId());
        Boolean ehAdmin = usuarioAutenticado.getPermissoes().stream()
                .anyMatch(permissao -> "ADMIN".equals(permissao.getNome()));

        if (!ehProprietario && !ehAdmin) {

            throw new AccessDeniedException("Você não possui permissão para remover este anúncio");
        }
    }
}
