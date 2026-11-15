package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.anuncio.validator.AnuncioSemPedidosValidatorService;
import br.com.guildcode.geekhub.service.anuncio.validator.ProprietarioAnuncioValidatorService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;

@Service
public class RemoverAnuncioService {

    private final BuscarAnuncioService buscarAnuncioService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final ProprietarioAnuncioValidatorService proprietarioAnuncioValidatorService;
    private final AnuncioSemPedidosValidatorService anuncioSemPedidosValidatorService;
    private final AnuncioRepository anuncioRepository;

    public RemoverAnuncioService(
            BuscarAnuncioService buscarAnuncioService,
            UsuarioAutenticadoService usuarioAutenticadoService,
            ProprietarioAnuncioValidatorService proprietarioAnuncioValidatorService,
            AnuncioSemPedidosValidatorService anuncioSemPedidosValidatorService,
            AnuncioRepository anuncioRepository) {

        this.buscarAnuncioService = buscarAnuncioService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.proprietarioAnuncioValidatorService = proprietarioAnuncioValidatorService;
        this.anuncioSemPedidosValidatorService = anuncioSemPedidosValidatorService;
        this.anuncioRepository = anuncioRepository;
    }

    public void remover(Long id) {

        Anuncio anuncio = buscarAnuncioService.buscarPorId(id);
        Usuario usuarioAutenticado = usuarioAutenticadoService.get();

        proprietarioAnuncioValidatorService.validarProprietarioOuAdmin(anuncio, usuarioAutenticado);
        anuncioSemPedidosValidatorService.validar(anuncio.getId());

        anuncioRepository.delete(anuncio);
    }
}
