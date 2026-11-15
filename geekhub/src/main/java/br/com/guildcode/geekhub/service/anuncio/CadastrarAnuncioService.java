package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.CadastrarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.CadastrarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.anuncio.CadastrarAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.anuncio.validator.CategoriaAtivaValidatorService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.categoria.BuscarCategoriaService;
import br.com.guildcode.geekhub.validator.anuncio.CadastrarAnuncioValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

@Service
public class CadastrarAnuncioService {

    private final CadastrarAnuncioValidator cadastrarAnuncioValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarCategoriaService buscarCategoriaService;
    private final CategoriaAtivaValidatorService categoriaAtivaValidatorService;
    private final CadastrarAnuncioMapper cadastrarAnuncioMapper;
    private final AnuncioRepository anuncioRepository;

    public CadastrarAnuncioService(
            CadastrarAnuncioValidator cadastrarAnuncioValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarCategoriaService buscarCategoriaService,
            CategoriaAtivaValidatorService categoriaAtivaValidatorService,
            CadastrarAnuncioMapper cadastrarAnuncioMapper,
            AnuncioRepository anuncioRepository) {

        this.cadastrarAnuncioValidator = cadastrarAnuncioValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarCategoriaService = buscarCategoriaService;
        this.categoriaAtivaValidatorService = categoriaAtivaValidatorService;
        this.cadastrarAnuncioMapper = cadastrarAnuncioMapper;
        this.anuncioRepository = anuncioRepository;
    }

    public CadastrarAnuncioResponse cadastrar(CadastrarAnuncioRequest request) {

        cadastrarAnuncioValidator.validarDadosCadastro(request);

        Usuario vendedor = usuarioAutenticadoService.get();
        Categoria categoria = buscarCategoriaService.buscarPorId(request.categoriaId());
        categoriaAtivaValidatorService.validar(categoria);

        Anuncio anuncio = cadastrarAnuncioMapper.toEntity(request);
        anuncio.setCategoria(categoria);
        anuncio.setVendedor(vendedor);
        anuncio.setDisponivel(TRUE);
        anuncio.setCriadoEm(LocalDateTime.now());

        anuncioRepository.save(anuncio);

        return cadastrarAnuncioMapper.toResponse(anuncio);
    }
}
