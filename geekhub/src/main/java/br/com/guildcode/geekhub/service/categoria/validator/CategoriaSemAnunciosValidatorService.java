package br.com.guildcode.geekhub.service.categoria.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class CategoriaSemAnunciosValidatorService {

    private final AnuncioRepository anuncioRepository;

    public CategoriaSemAnunciosValidatorService(AnuncioRepository anuncioRepository) {

        this.anuncioRepository = anuncioRepository;
    }

    public void validar(Long categoriaId) {

        if (TRUE.equals(anuncioRepository.existsByCategoriaId(categoriaId))) {

            throw new ConflitoException("Não é possível remover categoria com anúncios vinculados");
        }
    }
}
