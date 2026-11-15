package br.com.guildcode.geekhub.service.categoria.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.categoria.CategoriaRepository;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class CategoriaUnicaValidatorService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaUnicaValidatorService(CategoriaRepository categoriaRepository) {

        this.categoriaRepository = categoriaRepository;
    }

    public void validar(String nome) {

        if (TRUE.equals(categoriaRepository.existsByNomeIgnoreCase(nome))) {

            throw new ConflitoException("Categoria já cadastrada");
        }
    }
}
