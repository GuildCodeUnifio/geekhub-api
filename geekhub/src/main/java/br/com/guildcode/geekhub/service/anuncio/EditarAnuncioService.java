package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.request.EditarAnuncioRequest;
import br.com.guildcode.geekhub.controller.anuncio.dto.response.EditarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.anuncio.EditarAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.anuncio.validator.CategoriaAtivaValidatorService;
import br.com.guildcode.geekhub.service.anuncio.validator.ProprietarioAnuncioValidatorService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.categoria.BuscarCategoriaService;
import br.com.guildcode.geekhub.validator.anuncio.EditarAnuncioValidator;
import org.springframework.stereotype.Service;

@Service
public class EditarAnuncioService {

    private final EditarAnuncioValidator editarAnuncioValidator;
    private final BuscarAnuncioService buscarAnuncioService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final ProprietarioAnuncioValidatorService proprietarioAnuncioValidatorService;
    private final BuscarCategoriaService buscarCategoriaService;
    private final CategoriaAtivaValidatorService categoriaAtivaValidatorService;
    private final AnuncioRepository anuncioRepository;
    private final EditarAnuncioMapper editarAnuncioMapper;

    public EditarAnuncioService(
            EditarAnuncioValidator editarAnuncioValidator,
            BuscarAnuncioService buscarAnuncioService,
            UsuarioAutenticadoService usuarioAutenticadoService,
            ProprietarioAnuncioValidatorService proprietarioAnuncioValidatorService,
            BuscarCategoriaService buscarCategoriaService,
            CategoriaAtivaValidatorService categoriaAtivaValidatorService,
            AnuncioRepository anuncioRepository,
            EditarAnuncioMapper editarAnuncioMapper) {

        this.editarAnuncioValidator = editarAnuncioValidator;
        this.buscarAnuncioService = buscarAnuncioService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.proprietarioAnuncioValidatorService = proprietarioAnuncioValidatorService;
        this.buscarCategoriaService = buscarCategoriaService;
        this.categoriaAtivaValidatorService = categoriaAtivaValidatorService;
        this.anuncioRepository = anuncioRepository;
        this.editarAnuncioMapper = editarAnuncioMapper;
    }

    public EditarAnuncioResponse editar(Long id, EditarAnuncioRequest request) {

        editarAnuncioValidator.validarDadosEdicao(request);

        Anuncio anuncio = buscarAnuncioService.buscarPorId(id);
        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        proprietarioAnuncioValidatorService.validarProprietario(anuncio, usuarioAutenticado);

        Categoria categoria = buscarCategoriaService.buscarPorId(request.categoriaId());
        categoriaAtivaValidatorService.validar(categoria);

        anuncio.setNome(request.nome());
        anuncio.setDescricao(request.descricao());
        anuncio.setCategoria(categoria);
        anuncio.setPreco(request.preco());
        anuncio.setQuantidadeDisponivel(request.quantidadeDisponivel());
        anuncio.setDisponivel(request.disponivel());
        anuncio.getImagens().clear();
        anuncio.getImagens().addAll(request.imagens());
        anuncio.setCondicao(request.condicao());
        anuncio.setConservacao(request.conservacao());

        anuncioRepository.save(anuncio);

        return editarAnuncioMapper.toResponse(anuncio);
    }
}
