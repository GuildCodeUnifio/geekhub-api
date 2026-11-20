package br.com.guildcode.geekhub.service.carrinho.validator;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import org.springframework.stereotype.Service;

import static java.lang.Boolean.TRUE;

@Service
public class VendedorUnicoCarrinhoValidatorService {

    private final ItemCarrinhoRepository itemCarrinhoRepository;

    public VendedorUnicoCarrinhoValidatorService(ItemCarrinhoRepository itemCarrinhoRepository) {

        this.itemCarrinhoRepository = itemCarrinhoRepository;
    }

    public void validar(Usuario comprador, Anuncio anuncio) {

        if (TRUE.equals(itemCarrinhoRepository.existsByCompradorIdAndAnuncioVendedorIdNot(
                comprador.getId(), anuncio.getVendedor().getId()))) {

            throw new ConflitoException(
                    "O carrinho já possui itens de outro vendedor. Finalize ou esvazie o carrinho antes de adicionar produtos de um vendedor diferente");
        }
    }
}
