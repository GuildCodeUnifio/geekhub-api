package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.CriarPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.EventoPedidoResponse;
import br.com.guildcode.geekhub.controller.pedido.dto.response.ItemPedidoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.pedido.ItemPedido;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.pedido.CriarPedidoMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.pedido.validator.CarrinhoNaoVazioValidatorService;
import br.com.guildcode.geekhub.service.pedido.validator.EstoqueDisponivelPedidoValidatorService;
import br.com.guildcode.geekhub.service.pedido.validator.VendedorUnicoPedidoValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CRIADO;
import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarPedidoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private CarrinhoNaoVazioValidatorService carrinhoNaoVazioValidatorService;

    @Mock
    private VendedorUnicoPedidoValidatorService vendedorUnicoPedidoValidatorService;

    @Mock
    private EstoqueDisponivelPedidoValidatorService estoqueDisponivelPedidoValidatorService;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CriarPedidoMapper criarPedidoMapper;

    @InjectMocks
    private CriarPedidoService service;

    @Test
    @DisplayName("Deve criar pedido, descontar estoque dos anúncios e esvaziar carrinho com sucesso")
    void deveCriarPedidoBaixarEstoqueELimparCarrinhoComSucesso() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho itemCarrinho = ItemCarrinhoFactory.get();
        Anuncio anuncio = itemCarrinho.getAnuncio();
        anuncio.setQuantidadeDisponivel(2);
        itemCarrinho.setQuantidade(2);
        List<ItemCarrinho> itensCarrinho = List.of(itemCarrinho);

        LocalDateTime agora = LocalDateTime.of(2026, 11, 15, 20, 0);
        ItemPedido itemPedido = ItemPedido.builder()
                .anuncioId(anuncio.getId())
                .vendedorId(anuncio.getVendedor().getId())
                .nomeProduto(anuncio.getNome())
                .imagemProduto("https://picsum.photos/200")
                .precoUnitario(anuncio.getPreco())
                .quantidade(2)
                .subtotal(new BigDecimal("3700.00"))
                .build();
        List<ItemPedido> itensPedido = List.of(itemPedido);

        Pedido pedido = Pedido.builder()
                .id(1L)
                .comprador(comprador)
                .compradorNome(comprador.getNome())
                .compradorEmail(comprador.getEmail())
                .itens(new ArrayList<>(itensPedido))
                .avaliado(FALSE)
                .criadoEm(agora)
                .build();

        CriarPedidoResponse responseEsperada = CriarPedidoResponse.builder()
                .id(1L)
                .compradorId(comprador.getId())
                .compradorNome(comprador.getNome())
                .compradorEmail(comprador.getEmail())
                .itens(List.of(ItemPedidoResponse.builder()
                        .anuncioId(1L)
                        .vendedorId(2L)
                        .nomeProduto(anuncio.getNome())
                        .imagemProduto("https://picsum.photos/200")
                        .precoUnitario(anuncio.getPreco())
                        .quantidade(2)
                        .subtotal(new BigDecimal("3700.00"))
                        .build()))
                .valorTotal(new BigDecimal("3700.00"))
                .estado(CRIADO)
                .historico(List.of(EventoPedidoResponse.builder()
                        .estado(CRIADO)
                        .ocorridoEm(agora)
                        .build()))
                .avaliado(FALSE)
                .criadoEm(agora)
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(comprador.getId()))
                .thenReturn(itensCarrinho);
        when(criarPedidoMapper.toItemPedido(itemCarrinho)).thenReturn(itemPedido);
        when(criarPedidoMapper.toEntity(eq(comprador), eq(itensPedido), any(LocalDateTime.class)))
                .thenReturn(pedido);
        when(pedidoRepository.save(pedido)).thenReturn(pedido);
        when(criarPedidoMapper.toResponse(pedido)).thenReturn(responseEsperada);

        // Act
        CriarPedidoResponse resultado = service.criar();

        // Assert
        assertEquals(responseEsperada, resultado);
        assertEquals(new BigDecimal("3700.00"), pedido.getValorTotal());
        assertEquals(CRIADO, pedido.getEstado());
        assertEquals(1, pedido.getHistorico().size());
        assertEquals(CRIADO, pedido.getHistorico().getFirst().getEstado());
        assertEquals(0, anuncio.getQuantidadeDisponivel());
        assertEquals(FALSE, anuncio.getDisponivel());
        verify(carrinhoNaoVazioValidatorService).validar(itensCarrinho);
        verify(vendedorUnicoPedidoValidatorService).validar(itensCarrinho);
        verify(estoqueDisponivelPedidoValidatorService).validar(itensCarrinho);
        verify(anuncioRepository).save(anuncio);
        verify(pedidoRepository).save(pedido);
        verify(itemCarrinhoRepository).deleteByCompradorId(comprador.getId());
    }

    @Test
    @DisplayName("Deve impedir criação de pedido quando carrinho estiver vazio")
    void deveImpedirCriacaoQuandoCarrinhoEstiverVazio() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        List<ItemCarrinho> itensVazios = Collections.emptyList();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(comprador.getId()))
                .thenReturn(itensVazios);
        doThrow(new NegocioException("O carrinho de compras está vazio"))
                .when(carrinhoNaoVazioValidatorService)
                .validar(itensVazios);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.criar());
        verifyNoInteractions(vendedorUnicoPedidoValidatorService, estoqueDisponivelPedidoValidatorService,
                anuncioRepository, pedidoRepository, criarPedidoMapper);
        verify(itemCarrinhoRepository, never()).deleteByCompradorId(any());
    }

    @Test
    @DisplayName("Deve impedir criação de pedido quando houver itens de vendedores diferentes")
    void deveImpedirCriacaoQuandoHouverItensDeVendedoresDiferentes() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho itemCarrinho = ItemCarrinhoFactory.get();
        List<ItemCarrinho> itensCarrinho = List.of(itemCarrinho);

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(comprador.getId()))
                .thenReturn(itensCarrinho);
        doThrow(new ConflitoException("Todos os itens do pedido devem pertencer ao mesmo vendedor"))
                .when(vendedorUnicoPedidoValidatorService)
                .validar(itensCarrinho);

        // Act & Assert
        assertThrows(ConflitoException.class, () -> service.criar());
        verifyNoInteractions(estoqueDisponivelPedidoValidatorService, anuncioRepository,
                pedidoRepository, criarPedidoMapper);
        verify(itemCarrinhoRepository, never()).deleteByCompradorId(any());
    }

    @Test
    @DisplayName("Deve impedir criação de pedido sem alterar estoque nem carrinho quando houver conflito de estoque")
    void deveImpedirCriacaoQuandoHouverConflitoDeEstoque() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho itemCarrinho = ItemCarrinhoFactory.get();
        List<ItemCarrinho> itensCarrinho = List.of(itemCarrinho);

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(comprador.getId()))
                .thenReturn(itensCarrinho);
        doThrow(new ConflitoException("Estoque insuficiente para o anúncio 'Action figure Naruto 1ª edição'"))
                .when(estoqueDisponivelPedidoValidatorService)
                .validar(itensCarrinho);

        // Act & Assert
        assertThrows(ConflitoException.class, () -> service.criar());
        verifyNoInteractions(anuncioRepository, pedidoRepository, criarPedidoMapper);
        verify(itemCarrinhoRepository, never()).deleteByCompradorId(any());
    }
}
