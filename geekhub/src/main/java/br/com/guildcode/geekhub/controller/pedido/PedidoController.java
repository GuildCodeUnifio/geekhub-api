package br.com.guildcode.geekhub.controller.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.DetalharPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarCompraResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarPedidoAdminResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarVendaResponse;
import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.openapi.pedido.PedidoControllerOpenApi;
import br.com.guildcode.geekhub.service.pedido.CriarPedidoService;
import br.com.guildcode.geekhub.service.pedido.DetalharPedidoService;
import br.com.guildcode.geekhub.service.pedido.ListarMinhasComprasService;
import br.com.guildcode.geekhub.service.pedido.ListarMinhasVendasService;
import br.com.guildcode.geekhub.service.pedido.ListarTodosPedidosService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/pedidos")
public class PedidoController implements PedidoControllerOpenApi {

    private final CriarPedidoService criarPedidoService;
    private final ListarMinhasComprasService listarMinhasComprasService;
    private final ListarMinhasVendasService listarMinhasVendasService;
    private final ListarTodosPedidosService listarTodosPedidosService;
    private final DetalharPedidoService detalharPedidoService;

    public PedidoController(
            CriarPedidoService criarPedidoService,
            ListarMinhasComprasService listarMinhasComprasService,
            ListarMinhasVendasService listarMinhasVendasService,
            ListarTodosPedidosService listarTodosPedidosService,
            DetalharPedidoService detalharPedidoService) {

        this.criarPedidoService = criarPedidoService;
        this.listarMinhasComprasService = listarMinhasComprasService;
        this.listarMinhasVendasService = listarMinhasVendasService;
        this.listarTodosPedidosService = listarTodosPedidosService;
        this.detalharPedidoService = detalharPedidoService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CriarPedidoResponse criar() {

        return criarPedidoService.criar();
    }

    @GetMapping("/compras")
    @ResponseStatus(OK)
    public Page<ListarCompraResponse> listarCompras(
            @RequestParam(required = false) EstadoPedido estado,
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarMinhasComprasService.listar(estado, pageable);
    }

    @GetMapping("/vendas")
    @ResponseStatus(OK)
    public Page<ListarVendaResponse> listarVendas(
            @RequestParam(required = false) EstadoPedido estado,
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarMinhasVendasService.listar(estado, pageable);
    }

    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Page<ListarPedidoAdminResponse> listarTodos(
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarTodosPedidosService.listar(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public DetalharPedidoResponse detalhar(@PathVariable Long id) {

        return detalharPedidoService.detalhar(id);
    }
}
