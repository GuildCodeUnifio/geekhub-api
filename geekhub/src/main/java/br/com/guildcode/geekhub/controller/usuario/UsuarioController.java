package br.com.guildcode.geekhub.controller.usuario;

import br.com.guildcode.geekhub.controller.usuario.request.CadastrarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.request.EditarUsuarioRequest;
import br.com.guildcode.geekhub.controller.usuario.response.CadastrarUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.response.DetalharUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.response.EditarUsuarioResponse;
import br.com.guildcode.geekhub.controller.usuario.response.ListarUsuarioResponse;
import br.com.guildcode.geekhub.openapi.usuario.UsuarioControllerOpenApi;
import br.com.guildcode.geekhub.service.usuario.CadastrarUsuarioService;
import br.com.guildcode.geekhub.service.usuario.DetalharUsuarioService;
import br.com.guildcode.geekhub.service.usuario.EditarUsuarioService;
import br.com.guildcode.geekhub.service.usuario.ListarUsuariosService;
import br.com.guildcode.geekhub.service.usuario.RemoverUsuarioService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController implements UsuarioControllerOpenApi {

    private final CadastrarUsuarioService cadastrarUsuarioService;
    private final ListarUsuariosService listarUsuariosService;
    private final DetalharUsuarioService detalharUsuarioService;
    private final EditarUsuarioService editarUsuarioService;
    private final RemoverUsuarioService removerUsuarioService;

    public UsuarioController(
            CadastrarUsuarioService cadastrarUsuarioService,
            ListarUsuariosService listarUsuariosService,
            DetalharUsuarioService detalharUsuarioService,
            EditarUsuarioService editarUsuarioService,
            RemoverUsuarioService removerUsuarioService) {

        this.cadastrarUsuarioService = cadastrarUsuarioService;
        this.listarUsuariosService = listarUsuariosService;
        this.detalharUsuarioService = detalharUsuarioService;
        this.editarUsuarioService = editarUsuarioService;
        this.removerUsuarioService = removerUsuarioService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public CadastrarUsuarioResponse cadastrar(@Valid @RequestBody CadastrarUsuarioRequest request) {

        return cadastrarUsuarioService.cadastrar(request);
    }

    @GetMapping
    @ResponseStatus(OK)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Page<ListarUsuarioResponse> listar(@PageableDefault(size = 10, sort = "criadoEm", direction = DESC) Pageable pageable) {

        return listarUsuariosService.listar(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public DetalharUsuarioResponse detalhar(@PathVariable Long id) {

        return detalharUsuarioService.detalhar(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public EditarUsuarioResponse editar(@PathVariable Long id, @Valid @RequestBody EditarUsuarioRequest request) {

        return editarUsuarioService.editar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void remover(@PathVariable Long id) {

        removerUsuarioService.remover(id);
    }
}
