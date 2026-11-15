package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.springframework.stereotype.Service;

@Service
public class BuscarPedidoService {

    private final PedidoRepository pedidoRepository;

    public BuscarPedidoService(PedidoRepository pedidoRepository) {

        this.pedidoRepository = pedidoRepository;
    }

    public Pedido buscarPorId(Long id) {

        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));
    }
}
