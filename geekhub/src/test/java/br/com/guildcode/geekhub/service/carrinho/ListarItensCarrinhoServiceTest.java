package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.ListarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.carrinho.ListarItemCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarItensCarrinhoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private ListarItemCarrinhoMapper listarItemCarrinhoMapper;

    @InjectMocks
    private ListarItensCarrinhoService service;

    @Test
    @DisplayName("Deve listar itens paginados do carrinho do comprador autenticado")
    void deveListarItensPaginadosDoCompradorAutenticado() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemCarrinho> pageEntities = new PageImpl<>(List.of(item), pageable, 1);

        ListarItemCarrinhoResponse responseEsperada = ListarItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(1L)
                .vendedorId(2L)
                .nomeProduto(item.getAnuncio().getNome())
                .imagem("https://picsum.photos/200")
                .precoUnitario(item.getAnuncio().getPreco())
                .quantidade(item.getQuantidade())
                .quantidadeDisponivel(item.getAnuncio().getQuantidadeDisponivel())
                .disponivel(TRUE)
                .subtotal(new BigDecimal("3700.00"))
                .criadoEm(item.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(itemCarrinhoRepository.findByCompradorId(1L, pageable)).thenReturn(pageEntities);
        when(listarItemCarrinhoMapper.toResponse(item)).thenReturn(responseEsperada);

        // Act
        Page<ListarItemCarrinhoResponse> resultado = service.listar(pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(responseEsperada, resultado.getContent().getFirst());
        verify(itemCarrinhoRepository).findByCompradorId(1L, pageable);
    }
}
