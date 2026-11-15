package br.com.guildcode.geekhub.controller.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.CadastrarCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.DetalharCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.EditarCategoriaResponse;
import br.com.guildcode.geekhub.controller.categoria.dto.response.ListarCategoriaResponse;
import br.com.guildcode.geekhub.openapi.categoria.CategoriaControllerOpenApi;
import br.com.guildcode.geekhub.service.categoria.CadastrarCategoriaService;
import br.com.guildcode.geekhub.service.categoria.DetalharCategoriaService;
import br.com.guildcode.geekhub.service.categoria.EditarCategoriaService;
import br.com.guildcode.geekhub.service.categoria.ListarCategoriasService;
import br.com.guildcode.geekhub.service.categoria.RemoverCategoriaService;
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

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/categorias")
public class CategoriaController implements CategoriaControllerOpenApi {

    private final CadastrarCategoriaService cadastrarCategoriaService;
    private final ListarCategoriasService listarCategoriasService;
    private final DetalharCategoriaService detalharCategoriaService;
    private final EditarCategoriaService editarCategoriaService;
    private final RemoverCategoriaService removerCategoriaService;

    public CategoriaController(
            CadastrarCategoriaService cadastrarCategoriaService,
            ListarCategoriasService listarCategoriasService,
            DetalharCategoriaService detalharCategoriaService,
            EditarCategoriaService editarCategoriaService,
            RemoverCategoriaService removerCategoriaService) {

        this.cadastrarCategoriaService = cadastrarCategoriaService;
        this.listarCategoriasService = listarCategoriasService;
        this.detalharCategoriaService = detalharCategoriaService;
        this.editarCategoriaService = editarCategoriaService;
        this.removerCategoriaService = removerCategoriaService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CadastrarCategoriaResponse cadastrar(@Valid @RequestBody CadastrarCategoriaRequest request) {

        return cadastrarCategoriaService.cadastrar(request);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<ListarCategoriaResponse> listar(
            @RequestParam(required = false) Boolean ativa,
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarCategoriasService.listar(ativa, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public DetalharCategoriaResponse detalhar(@PathVariable Long id) {

        return detalharCategoriaService.detalhar(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public EditarCategoriaResponse editar(
            @PathVariable Long id,
            @Valid @RequestBody EditarCategoriaRequest request) {

        return editarCategoriaService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void remover(@PathVariable Long id) {

        removerCategoriaService.remover(id);
    }
}
