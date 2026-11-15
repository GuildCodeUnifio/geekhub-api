package br.com.guildcode.geekhub.service.carrinho.validator;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ProprietarioItemCarrinhoValidatorService {

    public void validarProprietario(ItemCarrinho item, Usuario usuarioAutenticado) {

        if (!item.getComprador().getId().equals(usuarioAutenticado.getId())) {
            throw new AccessDeniedException("Você não possui permissão para acessar este item do carrinho");
        }
    }
}
