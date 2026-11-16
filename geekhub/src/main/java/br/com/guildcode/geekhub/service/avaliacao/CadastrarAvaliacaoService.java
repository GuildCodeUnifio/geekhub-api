package br.com.guildcode.geekhub.service.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.CadastrarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.mapper.avaliacao.CadastrarAvaliacaoMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.avaliacao.validator.ElegibilidadeAvaliacaoValidatorService;
import br.com.guildcode.geekhub.service.pedido.BuscarPedidoService;
import br.com.guildcode.geekhub.service.usuario.BuscarUsuarioService;
import br.com.guildcode.geekhub.validator.avaliacao.CadastrarAvaliacaoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;

@Service
public class CadastrarAvaliacaoService {

    private final CadastrarAvaliacaoValidator cadastrarAvaliacaoValidator;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final BuscarPedidoService buscarPedidoService;
    private final ElegibilidadeAvaliacaoValidatorService elegibilidadeAvaliacaoValidatorService;
    private final BuscarUsuarioService buscarUsuarioService;
    private final CadastrarAvaliacaoMapper cadastrarAvaliacaoMapper;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PedidoRepository pedidoRepository;

    public CadastrarAvaliacaoService(
            CadastrarAvaliacaoValidator cadastrarAvaliacaoValidator,
            UsuarioAutenticadoService usuarioAutenticadoService,
            BuscarPedidoService buscarPedidoService,
            ElegibilidadeAvaliacaoValidatorService elegibilidadeAvaliacaoValidatorService,
            BuscarUsuarioService buscarUsuarioService,
            CadastrarAvaliacaoMapper cadastrarAvaliacaoMapper,
            AvaliacaoRepository avaliacaoRepository,
            PedidoRepository pedidoRepository) {

        this.cadastrarAvaliacaoValidator = cadastrarAvaliacaoValidator;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.buscarPedidoService = buscarPedidoService;
        this.elegibilidadeAvaliacaoValidatorService = elegibilidadeAvaliacaoValidatorService;
        this.buscarUsuarioService = buscarUsuarioService;
        this.cadastrarAvaliacaoMapper = cadastrarAvaliacaoMapper;
        this.avaliacaoRepository = avaliacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public CadastrarAvaliacaoResponse cadastrar(CadastrarAvaliacaoRequest request) {

        cadastrarAvaliacaoValidator.validarDadosCadastro(request);

        Usuario autor = usuarioAutenticadoService.get();
        Pedido pedido = buscarPedidoService.buscarPorId(request.pedidoId());

        elegibilidadeAvaliacaoValidatorService.validar(pedido, autor);

        Usuario vendedor = buscarUsuarioService.buscarPorId(pedido.obterVendedorId());

        Avaliacao avaliacao = cadastrarAvaliacaoMapper.toEntity(
                request, pedido, autor, vendedor, LocalDateTime.now());
        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

        pedido.setAvaliado(TRUE);
        pedidoRepository.save(pedido);

        return cadastrarAvaliacaoMapper.toResponse(avaliacaoSalva);
    }
}
