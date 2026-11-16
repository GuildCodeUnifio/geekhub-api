package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.response.ListarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.mapper.avaliacao.ListarAvaliacaoMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.nonNull;

@Service
public class ListarAvaliacoesService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ListarAvaliacaoMapper listarAvaliacaoMapper;

    public ListarAvaliacoesService(
            AvaliacaoRepository avaliacaoRepository,
            ListarAvaliacaoMapper listarAvaliacaoMapper) {

        this.avaliacaoRepository = avaliacaoRepository;
        this.listarAvaliacaoMapper = listarAvaliacaoMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarAvaliacaoResponse> listar(Long vendedorId, Pageable pageable) {

        Page<Avaliacao> avaliacoes = nonNull(vendedorId)
                ? avaliacaoRepository.findByVendedorId(vendedorId, pageable)
                : avaliacaoRepository.findAll(pageable);

        return avaliacoes.map(listarAvaliacaoMapper::toResponse);
    }
}
