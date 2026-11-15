package br.com.guildcode.geekhub.service.categoria;

import br.com.guildcode.geekhub.controller.categoria.dto.response.DetalharCategoriaResponse;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.mapper.categoria.DetalharCategoriaMapper;
import org.springframework.stereotype.Service;

@Service
public class DetalharCategoriaService {

    private final BuscarCategoriaService buscarCategoriaService;
    private final DetalharCategoriaMapper detalharCategoriaMapper;

    public DetalharCategoriaService(
            BuscarCategoriaService buscarCategoriaService,
            DetalharCategoriaMapper detalharCategoriaMapper) {

        this.buscarCategoriaService = buscarCategoriaService;
        this.detalharCategoriaMapper = detalharCategoriaMapper;
    }

    public DetalharCategoriaResponse detalhar(Long id) {

        Categoria categoria = buscarCategoriaService.buscarPorId(id);

        return detalharCategoriaMapper.toResponse(categoria);
    }
}
