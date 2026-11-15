package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import br.com.guildcode.geekhub.service.categoria.validator.CategoriaSemAnunciosValidatorService;
import org.springframework.stereotype.Service;

@Service
public class RemoverCategoriaService {

    private final BuscarCategoriaService buscarCategoriaService;
    private final CategoriaSemAnunciosValidatorService categoriaSemAnunciosValidatorService;
    private final CategoriaRepository categoriaRepository;

    public RemoverCategoriaService(
            BuscarCategoriaService buscarCategoriaService,
            CategoriaSemAnunciosValidatorService categoriaSemAnunciosValidatorService,
            CategoriaRepository categoriaRepository) {

        this.buscarCategoriaService = buscarCategoriaService;
        this.categoriaSemAnunciosValidatorService = categoriaSemAnunciosValidatorService;
        this.categoriaRepository = categoriaRepository;
    }

    public void remover(Long id) {

        Categoria categoria = buscarCategoriaService.buscarPorId(id);
        categoriaSemAnunciosValidatorService.validar(categoria.getId());

        categoriaRepository.delete(categoria);
    }
}
