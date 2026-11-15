package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.EditarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.EditarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.carrinho.EditarItemCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ProprietarioItemCarrinhoValidatorService;
import br.com.guildcode.geekhub.service.carrinho.validator.ValidarDisponibilidadeItemValidatorService;
import br.com.guildcode.geekhub.validator.carrinho.EditarItemCarrinhoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarItemCarrinhoService {

    private final EditarItemCarrinhoValidator editarItemCarrinhoValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarItemCarrinhoService buscarItemCarrinhoService;
    private final ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService;
    private final ValidarDisponibilidadeItemValidatorService validarDisponibilidadeItemValidatorService;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final EditarItemCarrinhoMapper editarItemCarrinhoMapper;

    public EditarItemCarrinhoService(
            EditarItemCarrinhoValidator editarItemCarrinhoValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarItemCarrinhoService buscarItemCarrinhoService,
            ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService,
            ValidarDisponibilidadeItemValidatorService validarDisponibilidadeItemValidatorService,
            ItemCarrinhoRepository itemCarrinhoRepository,
            EditarItemCarrinhoMapper editarItemCarrinhoMapper) {

        this.editarItemCarrinhoValidator = editarItemCarrinhoValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarItemCarrinhoService = buscarItemCarrinhoService;
        this.proprietarioItemCarrinhoValidatorService = proprietarioItemCarrinhoValidatorService;
        this.validarDisponibilidadeItemValidatorService = validarDisponibilidadeItemValidatorService;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.editarItemCarrinhoMapper = editarItemCarrinhoMapper;
    }

    @Transactional
    public EditarItemCarrinhoResponse editar(Long id, EditarItemCarrinhoRequest request) {

        editarItemCarrinhoValidator.validarDadosEdicao(request);

        Usuario comprador = usuarioAutenticadoService.get();
        ItemCarrinho item = buscarItemCarrinhoService.buscarPorId(id);

        proprietarioItemCarrinhoValidatorService.validarProprietario(item, comprador);
        validarDisponibilidadeItemValidatorService.validar(item.getAnuncio(), comprador, request.quantidade());

        item.setQuantidade(request.quantidade());
        ItemCarrinho itemAtualizado = itemCarrinhoRepository.save(item);

        return editarItemCarrinhoMapper.toResponse(itemAtualizado);
    }
}
