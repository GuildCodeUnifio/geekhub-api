package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AcessoPedidoValidatorService {

    public void validarVisualizacao(Pedido pedido, Usuario usuarioAutenticado) {

        Boolean isComprador = pedido.pertenceAoComprador(usuarioAutenticado.getId());
        Boolean isVendedor = pedido.possuiVendedor(usuarioAutenticado.getId());
        Boolean isAdmin = usuarioAutenticado.isAdmin();

        if (!isComprador && !isVendedor && !isAdmin) {
            throw new AccessDeniedException("Você não possui permissão para visualizar este pedido");
        }
    }
}
