package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.pedido.validator.PedidoSemAvaliacaoValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoverPedidoService {

    private final BuscarPedidoService buscarPedidoService;
    private final PedidoSemAvaliacaoValidatorService pedidoSemAvaliacaoValidatorService;
    private final PedidoRepository pedidoRepository;

    public RemoverPedidoService(
            BuscarPedidoService buscarPedidoService,
            PedidoSemAvaliacaoValidatorService pedidoSemAvaliacaoValidatorService,
            PedidoRepository pedidoRepository) {

        this.buscarPedidoService = buscarPedidoService;
        this.pedidoSemAvaliacaoValidatorService = pedidoSemAvaliacaoValidatorService;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public void remover(Long id) {

        Pedido pedido = buscarPedidoService.buscarPorId(id);
        pedidoSemAvaliacaoValidatorService.validar(pedido.getId());

        pedidoRepository.delete(pedido);
    }
}
