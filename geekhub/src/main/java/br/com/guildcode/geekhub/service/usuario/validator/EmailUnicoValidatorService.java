package br.com.guildcode.geekhub.service.usuario.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class EmailUnicoValidatorService {

    private final UsuarioRepository usuarioRepository;

    public EmailUnicoValidatorService(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    public void validar(String email) {

        if (TRUE.equals(usuarioRepository.existsByEmail(email))) {

            throw new ConflitoException("E-mail já cadastrado");
        }
    }
}
