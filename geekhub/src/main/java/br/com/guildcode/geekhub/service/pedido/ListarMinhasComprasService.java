package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarCompraResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.pedido.ListarCompraMapper;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarMinhasComprasService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final PedidoRepository pedidoRepository;
    private final ListarCompraMapper listarCompraMapper;

    public ListarMinhasComprasService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            PedidoRepository pedidoRepository,
            ListarCompraMapper listarCompraMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.pedidoRepository = pedidoRepository;
        this.listarCompraMapper = listarCompraMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarCompraResponse> listar(EstadoPedido estado, Pageable pageable) {

        Usuario comprador = usuarioAutenticadoService.get();

        Page<Pedido> pedidos = estado != null
                ? pedidoRepository.findByCompradorIdAndEstado(comprador.getId(), estado, pageable)
                : pedidoRepository.findByCompradorId(comprador.getId(), pageable);

        return pedidos.map(listarCompraMapper::toResponse);
    }
}
