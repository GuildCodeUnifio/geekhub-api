package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioAdminResponse;
import br.com.guildcode.geekhub.mapper.anuncio.ListarAnuncioAdminMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarAnunciosAdminService {

    private final AnuncioRepository anuncioRepository;
    private final ListarAnuncioAdminMapper listarAnuncioAdminMapper;

    public ListarAnunciosAdminService(
            AnuncioRepository anuncioRepository,
            ListarAnuncioAdminMapper listarAnuncioAdminMapper) {

        this.anuncioRepository = anuncioRepository;
        this.listarAnuncioAdminMapper = listarAnuncioAdminMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarAnuncioAdminResponse> listar(Pageable pageable) {

        return anuncioRepository.findAll(pageable)
                .map(listarAnuncioAdminMapper::toResponse);
    }
}
