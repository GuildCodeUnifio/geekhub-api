package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarVendaResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.pedido.ListarVendaMapper;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarMinhasVendasService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final PedidoRepository pedidoRepository;
    private final ListarVendaMapper listarVendaMapper;

    public ListarMinhasVendasService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            PedidoRepository pedidoRepository,
            ListarVendaMapper listarVendaMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.pedidoRepository = pedidoRepository;
        this.listarVendaMapper = listarVendaMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarVendaResponse> listar(EstadoPedido estado, Pageable pageable) {

        Usuario vendedor = usuarioAutenticadoService.get();
        Page<Pedido> pedidos = pedidoRepository.listarVendas(vendedor.getId(), estado, pageable);

        return pedidos.map(listarVendaMapper::toResponse);
    }
}
