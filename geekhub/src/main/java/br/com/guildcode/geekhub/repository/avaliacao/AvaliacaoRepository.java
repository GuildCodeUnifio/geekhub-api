package br.com.guildcode.geekhub.repository.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
}
