package br.com.guildcode.geekhub.controller.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.openapi.pedido.PedidoControllerOpenApi;
import br.com.guildcode.geekhub.service.pedido.CriarPedidoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/pedidos")
public class PedidoController implements PedidoControllerOpenApi {

    private final CriarPedidoService criarPedidoService;

    public PedidoController(CriarPedidoService criarPedidoService) {

        this.criarPedidoService = criarPedidoService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CriarPedidoResponse criar() {

        return criarPedidoService.criar();
    }
}
