package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.request.CadastrarCategoriaRequest;
import br.com.guildcode.geekhub.controller.categoria.dto.response.CadastrarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.mapper.categoria.CadastrarCategoriaMapper;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.service.categoria.validator.CategoriaUnicaValidatorService;
import br.com.guildcode.geekhub.validator.categoria.CadastrarCategoriaValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

@Service
public class CadastrarCategoriaService {

    private final CadastrarCategoriaValidator cadastrarCategoriaValidator;
    private final CategoriaUnicaValidatorService categoriaUnicaValidatorService;
    private final CadastrarCategoriaMapper cadastrarCategoriaMapper;
    private final CategoriaRepository categoriaRepository;

    public CadastrarCategoriaService(
            CadastrarCategoriaValidator cadastrarCategoriaValidator,
            CategoriaUnicaValidatorService categoriaUnicaValidatorService,
            CadastrarCategoriaMapper cadastrarCategoriaMapper,
            CategoriaRepository categoriaRepository) {

        this.cadastrarCategoriaValidator = cadastrarCategoriaValidator;
        this.categoriaUnicaValidatorService = categoriaUnicaValidatorService;
        this.cadastrarCategoriaMapper = cadastrarCategoriaMapper;
        this.categoriaRepository = categoriaRepository;
    }

    public CadastrarCategoriaResponse cadastrar(CadastrarCategoriaRequest request) {

        cadastrarCategoriaValidator.validarDadosCadastro(request);
        categoriaUnicaValidatorService.validar(request.nome());

        Categoria categoria = cadastrarCategoriaMapper.toEntity(request);
        categoria.setAtiva(TRUE);
        categoria.setCriadoEm(LocalDateTime.now());

        categoriaRepository.save(categoria);

        return cadastrarCategoriaMapper.toResponse(categoria);
    }
}
