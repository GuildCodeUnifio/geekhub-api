package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarMeuAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.anuncio.ListarMeuAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarMeusAnunciosService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final AnuncioRepository anuncioRepository;
    private final ListarMeuAnuncioMapper listarMeuAnuncioMapper;

    public ListarMeusAnunciosService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            AnuncioRepository anuncioRepository,
            ListarMeuAnuncioMapper listarMeuAnuncioMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.anuncioRepository = anuncioRepository;
        this.listarMeuAnuncioMapper = listarMeuAnuncioMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarMeuAnuncioResponse> listar(Pageable pageable) {

        Usuario vendedor = usuarioAutenticadoService.get();
        Page<Anuncio> anuncios = anuncioRepository.findByVendedorId(vendedor.getId(), pageable);

        return anuncios.map(listarMeuAnuncioMapper::toResponse);
    }
}
