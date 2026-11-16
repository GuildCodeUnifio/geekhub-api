package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.exception.NegocioException;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CANCELADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONFIRMADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.ENVIADO;
import static java.util.Objects.isNull;

@Service
public class TransicaoEstadoPedidoValidatorService {

    private static final Map<EstadoPedido, Set<EstadoPedido>> TRANSICOES_PERMITIDAS = Map.of(
            CRIADO, EnumSet.of(CONFIRMADO, CANCELADO),
            CONFIRMADO, EnumSet.of(ENVIADO, CANCELADO),
            ENVIADO, EnumSet.of(CONCLUIDO),
            CONCLUIDO, EnumSet.noneOf(EstadoPedido.class),
            CANCELADO, EnumSet.noneOf(EstadoPedido.class)
    );

    public void validar(EstadoPedido estadoAtual, EstadoPedido novoEstado) {

        if (isNull(estadoAtual) || isNull(novoEstado)) {
            throw new NegocioException("O estado do pedido é obrigatório");
        }

        if (CONCLUIDO.equals(estadoAtual) || CANCELADO.equals(estadoAtual)) {
            throw new NegocioException("Não é possível alterar um pedido que já está " + estadoAtual);
        }

        Set<EstadoPedido> destinosPermitidos = TRANSICOES_PERMITIDAS.get(estadoAtual);
        if (!destinosPermitidos.contains(novoEstado)) {
            throw new NegocioException(
                    String.format("Transição de estado inválida de %s para %s", estadoAtual, novoEstado));
        }
    }
}
