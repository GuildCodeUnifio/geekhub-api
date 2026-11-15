package br.com.guildcode.geekhub.controller.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.AdicionarItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ConsultarCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.DetalharItemCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ListarItemCarrinhoResponse;
import br.com.guildcode.geekhub.openapi.carrinho.CarrinhoControllerOpenApi;
import br.com.guildcode.geekhub.service.carrinho.AdicionarItemCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.ConsultarCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.DetalharItemCarrinhoService;
import br.com.guildcode.geekhub.service.carrinho.ListarItensCarrinhoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/carrinho")
public class CarrinhoController implements CarrinhoControllerOpenApi {

    private final AdicionarItemCarrinhoService adicionarItemCarrinhoService;
    private final ConsultarCarrinhoService consultarCarrinhoService;
    private final ListarItensCarrinhoService listarItensCarrinhoService;
    private final DetalharItemCarrinhoService detalharItemCarrinhoService;

    public CarrinhoController(
            AdicionarItemCarrinhoService adicionarItemCarrinhoService,
            ConsultarCarrinhoService consultarCarrinhoService,
            ListarItensCarrinhoService listarItensCarrinhoService,
            DetalharItemCarrinhoService detalharItemCarrinhoService) {

        this.adicionarItemCarrinhoService = adicionarItemCarrinhoService;
        this.consultarCarrinhoService = consultarCarrinhoService;
        this.listarItensCarrinhoService = listarItensCarrinhoService;
        this.detalharItemCarrinhoService = detalharItemCarrinhoService;
    }

    @PostMapping("/itens")
    @ResponseStatus(CREATED)
    public AdicionarItemCarrinhoResponse adicionarItem(
            @Valid @RequestBody AdicionarItemCarrinhoRequest request) {

        return adicionarItemCarrinhoService.adicionar(request);
    }

    @GetMapping
    @ResponseStatus(OK)
    public ConsultarCarrinhoResponse consultar() {

        return consultarCarrinhoService.consultar();
    }

    @GetMapping("/itens")
    @ResponseStatus(OK)
    public Page<ListarItemCarrinhoResponse> listarItens(
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarItensCarrinhoService.listar(pageable);
    }

    @GetMapping("/itens/{id}")
    @ResponseStatus(OK)
    public DetalharItemCarrinhoResponse detalharItem(@PathVariable Long id) {

        return detalharItemCarrinhoService.detalhar(id);
    }
}
