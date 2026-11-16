package br.com.guildcode.geekhub.service.avaliacao.validator;

import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;
import static java.util.Objects.isNull;

@Service
public class ElegibilidadeAvaliacaoValidatorService {

    private final AvaliacaoRepository avaliacaoRepository;

    public ElegibilidadeAvaliacaoValidatorService(AvaliacaoRepository avaliacaoRepository) {

        this.avaliacaoRepository = avaliacaoRepository;
    }

    public void validar(Pedido pedido, Usuario usuarioAutenticado) {

        if (!pedido.pertenceAoComprador(usuarioAutenticado.getId())) {
            throw new AccessDeniedException("Você não possui permissão para avaliar este pedido");
        }

        if (!pedido.isConcluido()) {
            throw new NegocioException("Somente pedidos concluídos podem ser avaliados");
        }

        if (TRUE.equals(pedido.getAvaliado())
                || TRUE.equals(avaliacaoRepository.existsByPedidoId(pedido.getId()))) {
            throw new ConflitoException("Este pedido já foi avaliado");
        }

        if (isNull(pedido.obterVendedorId())) {
            throw new NegocioException("O pedido não possui itens para identificar o vendedor");
        }
    }
}
