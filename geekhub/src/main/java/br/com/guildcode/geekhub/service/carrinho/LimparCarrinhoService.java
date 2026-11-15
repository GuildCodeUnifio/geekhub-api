package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LimparCarrinhoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

    public LimparCarrinhoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            ItemCarrinhoRepository itemCarrinhoRepository) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
    }

    @Transactional
    public void limpar() {

        Usuario comprador = usuarioAutenticadoService.get();
        itemCarrinhoRepository.deleteByCompradorId(comprador.getId());
    }
}
