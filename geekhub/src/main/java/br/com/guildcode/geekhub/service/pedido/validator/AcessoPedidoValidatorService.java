package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;

@Service
public class AcessoPedidoValidatorService {

    public void validarVisualizacao(Pedido pedido, Usuario usuarioAutenticado) {

        if (!isParticipanteOuAdmin(pedido, usuarioAutenticado)) {
            throw new AccessDeniedException("Você não possui permissão para visualizar este pedido");
        }
    }

    public void validarAtualizacaoEstado(Pedido pedido, Usuario usuarioAutenticado, EstadoPedido novoEstado) {

        if (!isParticipanteOuAdmin(pedido, usuarioAutenticado)) {
            throw new AccessDeniedException("Você não possui permissão para atualizar este pedido");
        }

        if (!CANCELADO.equals(novoEstado) && !isVendedorOuAdmin(pedido, usuarioAutenticado)) {
            throw new AccessDeniedException("Você não possui permissão para avançar o estado deste pedido");
        }
    }

    private Boolean isParticipanteOuAdmin(Pedido pedido, Usuario usuarioAutenticado) {

        return pedido.pertenceAoComprador(usuarioAutenticado.getId())
                || isVendedorOuAdmin(pedido, usuarioAutenticado);
    }

    private Boolean isVendedorOuAdmin(Pedido pedido, Usuario usuarioAutenticado) {

        return pedido.possuiVendedor(usuarioAutenticado.getId())
                || usuarioAutenticado.isAdmin();
    }
}
