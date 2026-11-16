package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.response.DetalharAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.mapper.avaliacao.DetalharAvaliacaoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DetalharAvaliacaoService {

    private final BuscarAvaliacaoService buscarAvaliacaoService;
    private final DetalharAvaliacaoMapper detalharAvaliacaoMapper;

    public DetalharAvaliacaoService(
            BuscarAvaliacaoService buscarAvaliacaoService,
            DetalharAvaliacaoMapper detalharAvaliacaoMapper) {

        this.buscarAvaliacaoService = buscarAvaliacaoService;
        this.detalharAvaliacaoMapper = detalharAvaliacaoMapper;
    }

    @Transactional(readOnly = true)
    public DetalharAvaliacaoResponse detalhar(Long id) {

        Avaliacao avaliacao = buscarAvaliacaoService.buscarPorId(id);

        return detalharAvaliacaoMapper.toResponse(avaliacao);
    }
}
