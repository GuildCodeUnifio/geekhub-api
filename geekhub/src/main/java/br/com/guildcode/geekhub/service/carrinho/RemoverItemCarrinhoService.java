package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ProprietarioItemCarrinhoValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoverItemCarrinhoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarItemCarrinhoService buscarItemCarrinhoService;
    private final ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

    public RemoverItemCarrinhoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarItemCarrinhoService buscarItemCarrinhoService,
            ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService,
            ItemCarrinhoRepository itemCarrinhoRepository) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarItemCarrinhoService = buscarItemCarrinhoService;
        this.proprietarioItemCarrinhoValidatorService = proprietarioItemCarrinhoValidatorService;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
    }

    @Transactional
    public void remover(Long id) {

        Usuario comprador = usuarioAutenticadoService.get();
        ItemCarrinho item = buscarItemCarrinhoService.buscarPorId(id);

        proprietarioItemCarrinhoValidatorService.validarProprietario(item, comprador);

        itemCarrinhoRepository.delete(item);
    }
}
