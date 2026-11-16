package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.EditarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.EditarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.avaliacao.EditarAvaliacaoMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.avaliacao.validator.AutorAvaliacaoValidatorService;
import br.com.guildcode.geekhub.validator.avaliacao.EditarAvaliacaoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarAvaliacaoService {

    private final EditarAvaliacaoValidator editarAvaliacaoValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarAvaliacaoService buscarAvaliacaoService;
    private final AutorAvaliacaoValidatorService autorAvaliacaoValidatorService;
    private final AvaliacaoRepository avaliacaoRepository;
    private final EditarAvaliacaoMapper editarAvaliacaoMapper;

    public EditarAvaliacaoService(
            EditarAvaliacaoValidator editarAvaliacaoValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarAvaliacaoService buscarAvaliacaoService,
            AutorAvaliacaoValidatorService autorAvaliacaoValidatorService,
            AvaliacaoRepository avaliacaoRepository,
            EditarAvaliacaoMapper editarAvaliacaoMapper) {

        this.editarAvaliacaoValidator = editarAvaliacaoValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarAvaliacaoService = buscarAvaliacaoService;
        this.autorAvaliacaoValidatorService = autorAvaliacaoValidatorService;
        this.avaliacaoRepository = avaliacaoRepository;
        this.editarAvaliacaoMapper = editarAvaliacaoMapper;
    }

    @Transactional
    public EditarAvaliacaoResponse editar(Long id, EditarAvaliacaoRequest request) {

        editarAvaliacaoValidator.validarDadosEdicao(request);

        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        Avaliacao avaliacao = buscarAvaliacaoService.buscarPorId(id);

        autorAvaliacaoValidatorService.validarAutor(avaliacao, usuarioAutenticado);

        avaliacao.setNota(request.nota());
        avaliacao.setComentario(request.comentario());

        Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);

        return editarAvaliacaoMapper.toResponse(avaliacaoAtualizada);
    }
}
