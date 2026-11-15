package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.response.DetalharItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.carrinho.DetalharItemCarrinhoMapper;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ProprietarioItemCarrinhoValidatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalharItemCarrinhoServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarItemCarrinhoService buscarItemCarrinhoService;

    @Mock
    private ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService;

    @Mock
    private DetalharItemCarrinhoMapper detalharItemCarrinhoMapper;

    @InjectMocks
    private DetalharItemCarrinhoService service;

    @Test
    @DisplayName("Deve detalhar item do carrinho quando pertencer ao comprador autenticado")
    void deveDetalharItemQuandoPertencerAoCompradorAutenticado() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();
        DetalharItemCarrinhoResponse responseEsperada = DetalharItemCarrinhoResponse.builder()
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
        when(buscarItemCarrinhoService.buscarPorId(1L)).thenReturn(item);
        when(detalharItemCarrinhoMapper.toResponse(item)).thenReturn(responseEsperada);

        // Act
        DetalharItemCarrinhoResponse resultado = service.detalhar(1L);

        // Assert
        assertEquals(responseEsperada, resultado);
        verify(proprietarioItemCarrinhoValidatorService).validarProprietario(item, comprador);
    }

    @Test
    @DisplayName("Deve propagar AccessDeniedException quando item pertencer a outro comprador")
    void devePropagarAccessDeniedExceptionQuandoItemPertencerAOutroComprador() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(1L)).thenReturn(item);
        doThrow(new AccessDeniedException("Você não possui permissão para acessar este item do carrinho"))
                .when(proprietarioItemCarrinhoValidatorService)
                .validarProprietario(item, comprador);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> service.detalhar(1L));
        verifyNoInteractions(detalharItemCarrinhoMapper);
    }

    @Test
    @DisplayName("Deve propagar RecursoNaoEncontradoException quando item não existir")
    void devePropagarRecursoNaoEncontradoExceptionQuandoItemNaoExistir() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> service.detalhar(99L));
        verifyNoInteractions(proprietarioItemCarrinhoValidatorService, detalharItemCarrinhoMapper);
    }
}
