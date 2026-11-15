package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.DetalharPedidoResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.pedido.DetalharPedidoMapper;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.pedido.validator.AcessoPedidoValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DetalharPedidoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarPedidoService buscarPedidoService;
    private final AcessoPedidoValidatorService acessoPedidoValidatorService;
    private final DetalharPedidoMapper detalharPedidoMapper;

    public DetalharPedidoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarPedidoService buscarPedidoService,
            AcessoPedidoValidatorService acessoPedidoValidatorService,
            DetalharPedidoMapper detalharPedidoMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarPedidoService = buscarPedidoService;
        this.acessoPedidoValidatorService = acessoPedidoValidatorService;
        this.detalharPedidoMapper = detalharPedidoMapper;
    }

    @Transactional(readOnly = true)
    public DetalharPedidoResponse detalhar(Long id) {

        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        Pedido pedido = buscarPedidoService.buscarPorId(id);

        acessoPedidoValidatorService.validarVisualizacao(pedido, usuarioAutenticado);

        return detalharPedidoMapper.toResponse(pedido);
    }
}
