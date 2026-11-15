package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.ListarCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.mapper.categoria.ListarCategoriaMapper;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
public class ListarCategoriasService {

    private final CategoriaRepository categoriaRepository;
    private final ListarCategoriaMapper listarCategoriaMapper;

    public ListarCategoriasService(
            CategoriaRepository categoriaRepository,
            ListarCategoriaMapper listarCategoriaMapper) {

        this.categoriaRepository = categoriaRepository;
        this.listarCategoriaMapper = listarCategoriaMapper;
    }

    public Page<ListarCategoriaResponse> listar(Boolean ativa, Pageable pageable) {

        Page<Categoria> categorias = nonNull(ativa)
                ? categoriaRepository.findByAtiva(ativa, pageable)
                : categoriaRepository.findAll(pageable);

        return categorias.map(listarCategoriaMapper::toResponse);
    }
}
