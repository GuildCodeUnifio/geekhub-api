package br.com.guildcode.geekhub.repository.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    Boolean existsByCategoriaId(Long categoriaId);

    Page<Anuncio> findByVendedorId(Long vendedorId, Pageable pageable);
}
