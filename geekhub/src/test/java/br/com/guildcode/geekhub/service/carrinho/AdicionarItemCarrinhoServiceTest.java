package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.AdicionarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.AdicionarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.ConflitoException;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.carrinho.AdicionarItemCarrinhoRequestFactory;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.carrinho.AdicionarItemCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.anuncio.BuscarAnuncioService;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ValidarDisponibilidadeItemValidatorService;
import br.com.guildcode.geekhub.service.carrinho.validator.VendedorUnicoCarrinhoValidatorService;
import br.com.guildcode.geekhub.validator.carrinho.AdicionarItemCarrinhoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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
class AdicionarItemCarrinhoServiceTest {

    @Mock
    private AdicionarItemCarrinhoValidator adicionarItemCarrinhoValidator;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarAnuncioService buscarAnuncioService;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private VendedorUnicoCarrinhoValidatorService vendedorUnicoCarrinhoValidatorService;

    @Mock
    private ValidarDisponibilidadeItemValidatorService validarDisponibilidadeItemValidatorService;

    @Mock
    private AdicionarItemCarrinhoMapper adicionarItemCarrinhoMapper;

    @InjectMocks
    private AdicionarItemCarrinhoService service;

    @Test
    @DisplayName("Deve adicionar novo item ao carrinho quando anúncio ainda não existir no carrinho")
    void deveAdicionarNovoItemAoCarrinhoQuandoNaoExistirNoCarrinho() {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();
        AdicionarItemCarrinhoResponse responseEsperada = AdicionarItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(1L)
                .nomeProduto(anuncio.getNome())
                .precoUnitario(anuncio.getPreco())
                .quantidade(2)
                .subtotal(new BigDecimal("3700.00"))
                .criadoEm(item.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarAnuncioService.buscarPorId(1L)).thenReturn(anuncio);
        when(itemCarrinhoRepository.findByCompradorIdAndAnuncioId(1L, 1L)).thenReturn(Optional.empty());
        when(adicionarItemCarrinhoMapper.toEntity(eq(comprador), eq(anuncio), eq(2), any(LocalDateTime.class)))
                .thenReturn(item);
        when(itemCarrinhoRepository.save(item)).thenReturn(item);
        when(adicionarItemCarrinhoMapper.toResponse(item)).thenReturn(responseEsperada);

        // Act
        AdicionarItemCarrinhoResponse resultado = service.adicionar(request);

        // Assert
        assertEquals(responseEsperada, resultado);
        verify(adicionarItemCarrinhoValidator).validarDadosAdicao(request);
        verify(vendedorUnicoCarrinhoValidatorService).validar(comprador, anuncio);
        verify(validarDisponibilidadeItemValidatorService).validar(anuncio, comprador, 2);
        verify(itemCarrinhoRepository).save(item);
    }

    @Test
    @DisplayName("Deve somar quantidade quando anúncio já existir no carrinho do comprador")
    void deveSomarQuantidadeQuandoAnuncioJaExistirNoCarrinho() {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();
        ItemCarrinho itemExistente = ItemCarrinhoFactory.get();
        itemExistente.setQuantidade(1);
        AdicionarItemCarrinhoResponse responseEsperada = AdicionarItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(1L)
                .nomeProduto(anuncio.getNome())
                .precoUnitario(anuncio.getPreco())
                .quantidade(3)
                .subtotal(new BigDecimal("5550.00"))
                .criadoEm(itemExistente.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarAnuncioService.buscarPorId(1L)).thenReturn(anuncio);
        when(itemCarrinhoRepository.findByCompradorIdAndAnuncioId(1L, 1L))
                .thenReturn(Optional.of(itemExistente));
        when(itemCarrinhoRepository.save(itemExistente)).thenReturn(itemExistente);
        when(adicionarItemCarrinhoMapper.toResponse(itemExistente)).thenReturn(responseEsperada);

        // Act
        AdicionarItemCarrinhoResponse resultado = service.adicionar(request);

        // Assert
        assertEquals(responseEsperada, resultado);
        assertEquals(3, itemExistente.getQuantidade());
        verify(vendedorUnicoCarrinhoValidatorService).validar(comprador, anuncio);
        verify(validarDisponibilidadeItemValidatorService).validar(anuncio, comprador, 3);
        verify(adicionarItemCarrinhoMapper, never()).toEntity(any(), any(), any(), any());
        verify(itemCarrinhoRepository).save(itemExistente);
    }

    @Test
    @DisplayName("Deve lançar ConflitoException quando carrinho já possuir item de outro vendedor")
    void deveLancarConflitoQuandoCarrinhoPossuirOutroVendedor() {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarAnuncioService.buscarPorId(1L)).thenReturn(anuncio);
        doThrow(new ConflitoException(
                "O carrinho já possui itens de outro vendedor. Finalize ou esvazie o carrinho antes de adicionar produtos de um vendedor diferente"))
                .when(vendedorUnicoCarrinhoValidatorService)
                .validar(comprador, anuncio);

        // Act & Assert
        assertThrows(ConflitoException.class, () -> service.adicionar(request));
        verifyNoInteractions(itemCarrinhoRepository, validarDisponibilidadeItemValidatorService,
                adicionarItemCarrinhoMapper);
    }

    @Test
    @DisplayName("Deve lançar NegocioException quando quantidade acumulada exceder o estoque")
    void deveLancarNegocioExceptionQuandoQuantidadeAcumuladaExcederEstoque() {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();
        Anuncio anuncio = AnuncioFactory.get();
        ItemCarrinho itemExistente = ItemCarrinhoFactory.get();
        itemExistente.setQuantidade(2);

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarAnuncioService.buscarPorId(1L)).thenReturn(anuncio);
        when(itemCarrinhoRepository.findByCompradorIdAndAnuncioId(1L, 1L))
                .thenReturn(Optional.of(itemExistente));
        doThrow(new NegocioException("Quantidade solicitada excede o estoque disponível do anúncio"))
                .when(validarDisponibilidadeItemValidatorService)
                .validar(anuncio, comprador, 4);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.adicionar(request));
        verify(itemCarrinhoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando anúncio não for encontrado")
    void deveLancarRecursoNaoEncontradoExceptionQuandoAnuncioNaoExistir() {
        // Arrange
        AdicionarItemCarrinhoRequest request = AdicionarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarAnuncioService.buscarPorId(1L))
                .thenThrow(new RecursoNaoEncontradoException("Anúncio não encontrado"));

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> service.adicionar(request));
        verifyNoInteractions(vendedorUnicoCarrinhoValidatorService, itemCarrinhoRepository,
                validarDisponibilidadeItemValidatorService, adicionarItemCarrinhoMapper);
    }
}
