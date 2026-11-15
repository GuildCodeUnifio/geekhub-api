package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarPedidoAdminResponse;
import br.com.guildcode.geekhub.mapper.pedido.ListarPedidoAdminMapper;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarTodosPedidosService {

    private final PedidoRepository pedidoRepository;
    private final ListarPedidoAdminMapper listarPedidoAdminMapper;

    public ListarTodosPedidosService(
            PedidoRepository pedidoRepository,
            ListarPedidoAdminMapper listarPedidoAdminMapper) {

        this.pedidoRepository = pedidoRepository;
        this.listarPedidoAdminMapper = listarPedidoAdminMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarPedidoAdminResponse> listar(Pageable pageable) {

        return pedidoRepository.findAll(pageable)
                .map(listarPedidoAdminMapper::toResponse);
    }
}
