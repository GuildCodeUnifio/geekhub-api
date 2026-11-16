package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.avaliacao.validator.AutorAvaliacaoValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Boolean.FALSE;

@Service
public class RemoverAvaliacaoService {

    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarAvaliacaoService buscarAvaliacaoService;
    private final AutorAvaliacaoValidatorService autorAvaliacaoValidatorService;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PedidoRepository pedidoRepository;

    public RemoverAvaliacaoService(
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarAvaliacaoService buscarAvaliacaoService,
            AutorAvaliacaoValidatorService autorAvaliacaoValidatorService,
            AvaliacaoRepository avaliacaoRepository,
            PedidoRepository pedidoRepository) {

        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarAvaliacaoService = buscarAvaliacaoService;
        this.autorAvaliacaoValidatorService = autorAvaliacaoValidatorService;
        this.avaliacaoRepository = avaliacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public void remover(Long id) {

        Usuario usuarioAutenticado = usuarioAutenticadoService.get();
        Avaliacao avaliacao = buscarAvaliacaoService.buscarPorId(id);

        autorAvaliacaoValidatorService.validarAutorOuAdmin(avaliacao, usuarioAutenticado);

        Pedido pedido = avaliacao.getPedido();
        avaliacaoRepository.delete(avaliacao);

        pedido.setAvaliado(FALSE);
        pedidoRepository.save(pedido);
    }
}
