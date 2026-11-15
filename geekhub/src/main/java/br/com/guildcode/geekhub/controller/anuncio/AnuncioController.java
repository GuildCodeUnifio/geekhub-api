package br.com.guildcode.geekhub.controller.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.DetalharAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioAdminResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarMeuAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import br.com.guildcode.geekhub.openapi.anuncio.AnuncioControllerOpenApi;
import br.com.guildcode.geekhub.service.anuncio.CadastrarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.DetalharAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.EditarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.ListarAnunciosAdminService;
import br.com.guildcode.geekhub.service.anuncio.ListarMeusAnunciosService;
import br.com.guildcode.geekhub.service.anuncio.PesquisarAnunciosService;
import br.com.guildcode.geekhub.service.anuncio.RemoverAnuncioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/anuncios")
public class AnuncioController implements AnuncioControllerOpenApi {

    private final CadastrarAnuncioService cadastrarAnuncioService;
    private final PesquisarAnunciosService pesquisarAnunciosService;
    private final DetalharAnuncioService detalharAnuncioService;
    private final ListarMeusAnunciosService listarMeusAnunciosService;
    private final ListarAnunciosAdminService listarAnunciosAdminService;
    private final EditarAnuncioService editarAnuncioService;
    private final RemoverAnuncioService removerAnuncioService;

    public AnuncioController(
            CadastrarAnuncioService cadastrarAnuncioService,
            PesquisarAnunciosService pesquisarAnunciosService,
            DetalharAnuncioService detalharAnuncioService,
            ListarMeusAnunciosService listarMeusAnunciosService,
            ListarAnunciosAdminService listarAnunciosAdminService,
            EditarAnuncioService editarAnuncioService,
            RemoverAnuncioService removerAnuncioService) {

        this.cadastrarAnuncioService = cadastrarAnuncioService;
        this.pesquisarAnunciosService = pesquisarAnunciosService;
        this.detalharAnuncioService = detalharAnuncioService;
        this.listarMeusAnunciosService = listarMeusAnunciosService;
        this.listarAnunciosAdminService = listarAnunciosAdminService;
        this.editarAnuncioService = editarAnuncioService;
        this.removerAnuncioService = removerAnuncioService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CadastrarAnuncioResponse cadastrar(@Valid @RequestBody CadastrarAnuncioRequest request) {

        return cadastrarAnuncioService.cadastrar(request);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<ListarAnuncioResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) CondicaoProduto condicao,
            @RequestParam(required = false) ConservacaoProduto conservacao,
            @RequestParam(required = false) Long vendedorId,
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return pesquisarAnunciosService.pesquisar(
                nome, categoriaId, precoMin, precoMax, condicao, conservacao, vendedorId, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public DetalharAnuncioResponse detalhar(@PathVariable Long id) {

        return detalharAnuncioService.detalhar(id);
    }

    @GetMapping("/meus")
    @ResponseStatus(OK)
    public Page<ListarMeuAnuncioResponse> listarMeus(
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarMeusAnunciosService.listar(pageable);
    }

    @GetMapping("/admin")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Page<ListarAnuncioAdminResponse> listarAdmin(
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarAnunciosAdminService.listar(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public EditarAnuncioResponse editar(
            @PathVariable Long id,
            @Valid @RequestBody EditarAnuncioRequest request) {

        return editarAnuncioService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void remover(@PathVariable Long id) {

        removerAnuncioService.remover(id);
    }
}
