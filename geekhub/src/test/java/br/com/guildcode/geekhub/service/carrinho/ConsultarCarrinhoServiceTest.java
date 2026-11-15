package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ConsultarCarrinhoResponse;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.ItemCarrinhoDetalheResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.carrinho.ConsultarCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCarrinhoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private ConsultarCarrinhoMapper consultarCarrinhoMapper;

    @InjectMocks
    private ConsultarCarrinhoService service;

    @Test
    @DisplayName("Deve consultar carrinho com itens e calcular total corretamente")
    void deveConsultarCarrinhoComItensECalcularTotal() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item1 = ItemCarrinhoFactory.get();
        ItemCarrinho item2 = ItemCarrinhoFactory.get();
        item2.setId(2L);

        ItemCarrinhoDetalheResponse detalhe1 = ItemCarrinhoDetalheResponse.builder()
                .id(1L)
                .anuncioId(1L)
                .vendedorId(2L)
                .nome("Action figure Naruto")
                .imagem("https://picsum.photos/200")
                .precoUnitario(new BigDecimal("100.00"))
                .quantidade(1)
                .quantidadeDisponivel(3)
                .disponivel(TRUE)
                .subtotal(new BigDecimal("100.00"))
                .build();

        ItemCarrinhoDetalheResponse detalhe2 = ItemCarrinhoDetalheResponse.builder()
                .id(2L)
                .anuncioId(2L)
                .vendedorId(3L)
                .nome("Mangá Colecionável")
                .imagem("https://picsum.photos/200")
                .precoUnitario(new BigDecimal("49.95"))
                .quantidade(1)
                .quantidadeDisponivel(5)
                .disponivel(TRUE)
                .subtotal(new BigDecimal("49.95"))
                .build();

        BigDecimal totalEsperado = new BigDecimal("149.95");
        List<ItemCarrinhoDetalheResponse> itensEsperados = List.of(detalhe1, detalhe2);
        ConsultarCarrinhoResponse responseEsperada = ConsultarCarrinhoResponse.builder()
                .compradorId(1L)
                .itens(itensEsperados)
                .total(totalEsperado)
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(1L))
                .thenReturn(List.of(item1, item2));
        when(consultarCarrinhoMapper.toItemResponse(item1)).thenReturn(detalhe1);
        when(consultarCarrinhoMapper.toItemResponse(item2)).thenReturn(detalhe2);
        when(consultarCarrinhoMapper.toResponse(1L, itensEsperados, totalEsperado))
                .thenReturn(responseEsperada);

        // Act
        ConsultarCarrinhoResponse resultado = service.consultar();

        // Assert
        assertEquals(responseEsperada, resultado);
        verify(consultarCarrinhoMapper).toResponse(1L, itensEsperados, totalEsperado);
    }

    @Test
    @DisplayName("Deve retornar carrinho vazio com total zerado quando não houver itens")
    void deveRetornarCarrinhoVazioQuandoNaoHouverItens() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        BigDecimal totalEsperado = new BigDecimal("0.00");
        ConsultarCarrinhoResponse responseEsperada = ConsultarCarrinhoResponse.builder()
                .compradorId(1L)
                .itens(Collections.emptyList())
                .total(totalEsperado)
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(itemCarrinhoRepository.findByCompradorIdOrderByCriadoEmDesc(1L))
                .thenReturn(Collections.emptyList());
        when(consultarCarrinhoMapper.toResponse(1L, Collections.emptyList(), totalEsperado))
                .thenReturn(responseEsperada);

        // Act
        ConsultarCarrinhoResponse resultado = service.consultar();

        // Assert
        assertEquals(responseEsperada, resultado);
        verify(consultarCarrinhoMapper).toResponse(1L, Collections.emptyList(), totalEsperado);
        verify(consultarCarrinhoMapper, never()).toItemResponse(any());
    }
}
