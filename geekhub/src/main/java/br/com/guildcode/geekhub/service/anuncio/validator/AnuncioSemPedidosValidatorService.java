package br.com.guildcode.geekhub.service.anuncio.validator;

import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class AnuncioSemPedidosValidatorService {

    private final PedidoRepository pedidoRepository;

    public AnuncioSemPedidosValidatorService(PedidoRepository pedidoRepository) {

        this.pedidoRepository = pedidoRepository;
    }

    public void validar(Long anuncioId) {

        if (TRUE.equals(pedidoRepository.existsByAnuncioId(anuncioId))) {

            throw new ConflitoException("Não é possível remover anúncio pois existem pedidos vinculados");
        }
    }
}
