package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import org.springframework.stereotype.Service;

@Service
public class BuscarAnuncioService {

    private final AnuncioRepository anuncioRepository;

    public BuscarAnuncioService(AnuncioRepository anuncioRepository) {

        this.anuncioRepository = anuncioRepository;
    }

    public Anuncio buscarPorId(Long id) {

        return anuncioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Anúncio não encontrado"));
    }
}
