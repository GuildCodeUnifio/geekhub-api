package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.EditarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.EditarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.mapper.categoria.EditarCategoriaMapper;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.service.categoria.validator.CategoriaUnicaValidatorService;
import br.com.guildcode.geekhub.validator.categoria.EditarCategoriaValidator;
import org.springframework.stereotype.Service;

@Service
public class EditarCategoriaService {

    private final EditarCategoriaValidator editarCategoriaValidator;
    private final BuscarCategoriaService buscarCategoriaService;
    private final CategoriaUnicaValidatorService categoriaUnicaValidatorService;
    private final CategoriaRepository categoriaRepository;
    private final EditarCategoriaMapper editarCategoriaMapper;

    public EditarCategoriaService(
            EditarCategoriaValidator editarCategoriaValidator,
            BuscarCategoriaService buscarCategoriaService,
            CategoriaUnicaValidatorService categoriaUnicaValidatorService,
            CategoriaRepository categoriaRepository,
            EditarCategoriaMapper editarCategoriaMapper) {

        this.editarCategoriaValidator = editarCategoriaValidator;
        this.buscarCategoriaService = buscarCategoriaService;
        this.categoriaUnicaValidatorService = categoriaUnicaValidatorService;
        this.categoriaRepository = categoriaRepository;
        this.editarCategoriaMapper = editarCategoriaMapper;
    }

    public EditarCategoriaResponse editar(Long id, EditarCategoriaRequest request) {

        editarCategoriaValidator.validarDadosEdicao(request);

        Categoria categoria = buscarCategoriaService.buscarPorId(id);

        if (!categoria.getNome().equalsIgnoreCase(request.nome())) {
            categoriaUnicaValidatorService.validar(request.nome());
        }

        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        categoria.setAtiva(request.ativa());

        categoriaRepository.save(categoria);

        return editarCategoriaMapper.toResponse(categoria);
    }
}
