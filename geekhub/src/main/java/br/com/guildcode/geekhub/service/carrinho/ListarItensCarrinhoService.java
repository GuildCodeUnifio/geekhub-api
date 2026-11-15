package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ListarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.carrinho.ListarItemCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListarItensCarrinhoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ListarItemCarrinhoMapper listarItemCarrinhoMapper;

    public ListarItensCarrinhoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            ItemCarrinhoRepository itemCarrinhoRepository,
            ListarItemCarrinhoMapper listarItemCarrinhoMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.listarItemCarrinhoMapper = listarItemCarrinhoMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarItemCarrinhoResponse> listar(Pageable pageable) {

        Usuario comprador = usuarioAutenticadoService.get();

        return itemCarrinhoRepository.findByCompradorId(comprador.getId(), pageable)
                .map(listarItemCarrinhoMapper::toResponse);
    }
}
