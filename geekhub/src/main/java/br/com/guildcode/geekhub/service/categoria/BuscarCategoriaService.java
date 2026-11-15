package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class BuscarCategoriaService {

    private final CategoriaRepository categoriaRepository;

    public BuscarCategoriaService(CategoriaRepository categoriaRepository) {

        this.categoriaRepository = categoriaRepository;
    }

    public Categoria buscarPorId(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria não encontrada"));
    }
}
