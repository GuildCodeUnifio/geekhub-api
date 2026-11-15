package br.com.guildcode.geekhub.repository.categoria;

import br.com.guildcode.geekhub.domain.categoria.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Boolean existsByNomeIgnoreCase(String nome);

    Page<Categoria> findByAtiva(Boolean ativa, Pageable pageable);
}
