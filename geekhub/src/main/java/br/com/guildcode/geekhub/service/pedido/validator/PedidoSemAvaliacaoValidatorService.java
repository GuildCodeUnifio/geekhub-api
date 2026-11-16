package br.com.guildcode.geekhub.service.pedido.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class PedidoSemAvaliacaoValidatorService {

    private final AvaliacaoRepository avaliacaoRepository;

    public PedidoSemAvaliacaoValidatorService(AvaliacaoRepository avaliacaoRepository) {

        this.avaliacaoRepository = avaliacaoRepository;
    }

    public void validar(Long pedidoId) {

        if (TRUE.equals(avaliacaoRepository.existsByPedidoId(pedidoId))) {
            throw new ConflitoException("Não é possível remover pedido pois possui avaliação vinculada");
        }
    }
}
