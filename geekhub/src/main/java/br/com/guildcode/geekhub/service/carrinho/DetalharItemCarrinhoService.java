package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.DetalharItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.carrinho.DetalharItemCarrinhoMapper;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ProprietarioItemCarrinhoValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DetalharItemCarrinhoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarItemCarrinhoService buscarItemCarrinhoService;
    private final ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService;
    private final DetalharItemCarrinhoMapper detalharItemCarrinhoMapper;

    public DetalharItemCarrinhoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarItemCarrinhoService buscarItemCarrinhoService,
            ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService,
            DetalharItemCarrinhoMapper detalharItemCarrinhoMapper) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarItemCarrinhoService = buscarItemCarrinhoService;
        this.proprietarioItemCarrinhoValidatorService = proprietarioItemCarrinhoValidatorService;
        this.detalharItemCarrinhoMapper = detalharItemCarrinhoMapper;
    }

    @Transactional(readOnly = true)
    public DetalharItemCarrinhoResponse detalhar(Long id) {

        Usuario comprador = usuarioAutenticadoService.get();
        ItemCarrinho item = buscarItemCarrinhoService.buscarPorId(id);

        proprietarioItemCarrinhoValidatorService.validarProprietario(item, comprador);

        return detalharItemCarrinhoMapper.toResponse(item);
    }
}
