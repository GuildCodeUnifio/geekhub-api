package br.com.guildcode.geekhub.repository.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    Boolean existsByCategoriaId(Long categoriaId);
}
