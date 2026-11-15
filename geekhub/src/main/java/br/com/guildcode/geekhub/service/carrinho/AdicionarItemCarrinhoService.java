package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.AdicionarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.carrinho.AdicionarItemCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.anuncio.BuscarAnuncioService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ValidarDisponibilidadeItemValidatorService;
import br.com.guildcode.geekhub.validator.carrinho.AdicionarItemCarrinhoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdicionarItemCarrinhoService {

    private final AdicionarItemCarrinhoValidator adicionarItemCarrinhoValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarAnuncioService buscarAnuncioService;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ValidarDisponibilidadeItemValidatorService validarDisponibilidadeItemValidatorService;
    private final AdicionarItemCarrinhoMapper adicionarItemCarrinhoMapper;

    public AdicionarItemCarrinhoService(
            AdicionarItemCarrinhoValidator adicionarItemCarrinhoValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarAnuncioService buscarAnuncioService,
            ItemCarrinhoRepository itemCarrinhoRepository,
            ValidarDisponibilidadeItemValidatorService validarDisponibilidadeItemValidatorService,
            AdicionarItemCarrinhoMapper adicionarItemCarrinhoMapper) {

        this.adicionarItemCarrinhoValidator = adicionarItemCarrinhoValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarAnuncioService = buscarAnuncioService;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.validarDisponibilidadeItemValidatorService = validarDisponibilidadeItemValidatorService;
        this.adicionarItemCarrinhoMapper = adicionarItemCarrinhoMapper;
    }

    @Transactional
    public AdicionarItemCarrinhoResponse adicionar(AdicionarItemCarrinhoRequest request) {

        adicionarItemCarrinhoValidator.validarDadosAdicao(request);

        Usuario comprador = usuarioAutenticadoService.get();
        Anuncio anuncio = buscarAnuncioService.buscarPorId(request.anuncioId());

        Optional<ItemCarrinho> itemExistente =
                itemCarrinhoRepository.findByCompradorIdAndAnuncioId(comprador.getId(), anuncio.getId());

        ItemCarrinho item;
        if (itemExistente.isPresent()) {
            item = itemExistente.get();
            Integer novaQuantidade = item.getQuantidade() + request.quantidade();
            validarDisponibilidadeItemValidatorService.validar(anuncio, comprador, novaQuantidade);
            item.setQuantidade(novaQuantidade);
        } else {
            validarDisponibilidadeItemValidatorService.validar(anuncio, comprador, request.quantidade());
            item = adicionarItemCarrinhoMapper.toEntity(comprador, anuncio, request.quantidade(), LocalDateTime.now());
        }

        ItemCarrinho itemSalvo = itemCarrinhoRepository.save(item);

        return adicionarItemCarrinhoMapper.toResponse(itemSalvo);
    }
}
