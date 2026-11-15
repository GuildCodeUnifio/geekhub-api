package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class BuscarAvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;

    public BuscarAvaliacaoService(AvaliacaoRepository avaliacaoRepository) {

        this.avaliacaoRepository = avaliacaoRepository;
    }

    public Avaliacao buscarPorId(Long id) {

        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliação não encontrada"));
    }
}
