package br.com.guildcode.geekhub.controller.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.openapi.anuncio.AnuncioControllerOpenApi;
import br.com.guildcode.geekhub.service.anuncio.CadastrarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.EditarAnuncioService;
import br.com.guildcode.geekhub.service.anuncio.RemoverAnuncioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/anuncios")
public class AnuncioController implements AnuncioControllerOpenApi {

    private final CadastrarAnuncioService cadastrarAnuncioService;
    private final EditarAnuncioService editarAnuncioService;
    private final RemoverAnuncioService removerAnuncioService;

    public AnuncioController(
            CadastrarAnuncioService cadastrarAnuncioService,
            EditarAnuncioService editarAnuncioService,
            RemoverAnuncioService removerAnuncioService) {

        this.cadastrarAnuncioService = cadastrarAnuncioService;
        this.editarAnuncioService = editarAnuncioService;
        this.removerAnuncioService = removerAnuncioService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CadastrarAnuncioResponse cadastrar(@Valid @RequestBody CadastrarAnuncioRequest request) {

        return cadastrarAnuncioService.cadastrar(request);
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
