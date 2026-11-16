package br.com.guildcode.geekhub.controller.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.CadastrarAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.DetalharAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.EditarAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.ListarAvaliacaoResponse;
import br.com.guildcode.geekhub.openapi.avaliacao.AvaliacaoControllerOpenApi;
import br.com.guildcode.geekhub.service.avaliacao.CadastrarAvaliacaoService;
import br.com.guildcode.geekhub.service.avaliacao.DetalharAvaliacaoService;
import br.com.guildcode.geekhub.service.avaliacao.EditarAvaliacaoService;
import br.com.guildcode.geekhub.service.avaliacao.ListarAvaliacoesService;
import br.com.guildcode.geekhub.service.avaliacao.RemoverAvaliacaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/avaliacoes")
public class AvaliacaoController implements AvaliacaoControllerOpenApi {

    private final CadastrarAvaliacaoService cadastrarAvaliacaoService;
    private final ListarAvaliacoesService listarAvaliacoesService;
    private final DetalharAvaliacaoService detalharAvaliacaoService;
    private final EditarAvaliacaoService editarAvaliacaoService;
    private final RemoverAvaliacaoService removerAvaliacaoService;

    public AvaliacaoController(
            CadastrarAvaliacaoService cadastrarAvaliacaoService,
            ListarAvaliacoesService listarAvaliacoesService,
            DetalharAvaliacaoService detalharAvaliacaoService,
            EditarAvaliacaoService editarAvaliacaoService,
            RemoverAvaliacaoService removerAvaliacaoService) {

        this.cadastrarAvaliacaoService = cadastrarAvaliacaoService;
        this.listarAvaliacoesService = listarAvaliacoesService;
        this.detalharAvaliacaoService = detalharAvaliacaoService;
        this.editarAvaliacaoService = editarAvaliacaoService;
        this.removerAvaliacaoService = removerAvaliacaoService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CadastrarAvaliacaoResponse cadastrar(@Valid @RequestBody CadastrarAvaliacaoRequest request) {

        return cadastrarAvaliacaoService.cadastrar(request);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<ListarAvaliacaoResponse> listar(
            @RequestParam(required = false) Long vendedorId,
            @PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarAvaliacoesService.listar(vendedorId, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public DetalharAvaliacaoResponse detalhar(@PathVariable Long id) {

        return detalharAvaliacaoService.detalhar(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public EditarAvaliacaoResponse editar(
            @PathVariable Long id,
            @Valid @RequestBody EditarAvaliacaoRequest request) {

        return editarAvaliacaoService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void remover(@PathVariable Long id) {

        removerAvaliacaoService.remover(id);
    }
}
