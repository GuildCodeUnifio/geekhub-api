package br.com.guildcode.geekhub.service.carrinho;

import br.com.guildcode.geekhub.controller.carrinho.dto.request.EditarItemCarrinhoRequest;
import br.com.guildcode.geekhub.controller.carrinho.dto.response.EditarItemCarrinhoResponse;
import br.com.guildcode.geekhub.domain.carrinho.ItemCarrinho;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.exception.NegocioException;
import br.com.guildcode.geekhub.exception.RecursoNaoEncontradoException;
import br.com.guildcode.geekhub.factory.carrinho.EditarItemCarrinhoRequestFactory;
import br.com.guildcode.geekhub.factory.carrinho.ItemCarrinhoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.carrinho.EditarItemCarrinhoMapper;
import br.com.guildcode.geekhub.repository.carrinho.ItemCarrinhoRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
import br.com.guildcode.geekhub.service.carrinho.validator.ProprietarioItemCarrinhoValidatorService;
import br.com.guildcode.geekhub.service.carrinho.validator.ValidarDisponibilidadeItemValidatorService;
import br.com.guildcode.geekhub.validator.carrinho.EditarItemCarrinhoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditarItemCarrinhoServiceTest {

    @Mock
    private EditarItemCarrinhoValidator editarItemCarrinhoValidator;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private BuscarItemCarrinhoService buscarItemCarrinhoService;

    @Mock
    private ProprietarioItemCarrinhoValidatorService proprietarioItemCarrinhoValidatorService;

    @Mock
    private ValidarDisponibilidadeItemValidatorService validarDisponibilidadeItemValidatorService;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private EditarItemCarrinhoMapper editarItemCarrinhoMapper;

    @InjectMocks
    private EditarItemCarrinhoService service;

    @Test
    @DisplayName("Deve atualizar quantidade do item do carrinho e retornar subtotal recalculado")
    void deveAtualizarQuantidadeDoItemComSucesso() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();
        EditarItemCarrinhoResponse responseEsperada = EditarItemCarrinhoResponse.builder()
                .id(1L)
                .anuncioId(1L)
                .nomeProduto(item.getAnuncio().getNome())
                .precoUnitario(item.getAnuncio().getPreco())
                .quantidade(3)
                .subtotal(new BigDecimal("5550.00"))
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(1L)).thenReturn(item);
        when(itemCarrinhoRepository.save(item)).thenReturn(item);
        when(editarItemCarrinhoMapper.toResponse(item)).thenReturn(responseEsperada);

        // Act
        EditarItemCarrinhoResponse resultado = service.editar(1L, request);

        // Assert
        assertEquals(responseEsperada, resultado);
        assertEquals(3, item.getQuantidade());
        verify(editarItemCarrinhoValidator).validarDadosEdicao(request);
        verify(proprietarioItemCarrinhoValidatorService).validarProprietario(item, comprador);
        verify(validarDisponibilidadeItemValidatorService).validar(item.getAnuncio(), comprador, 3);
        verify(itemCarrinhoRepository).save(item);
    }

    @Test
    @DisplayName("Deve propagar NegocioException quando dados de edição forem inválidos")
    void devePropagarNegocioExceptionQuandoDadosEdicaoForemInvalidos() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequest.builder()
                .quantidade(0)
                .build();
        doThrow(new NegocioException("A quantidade deve ser maior que zero"))
                .when(editarItemCarrinhoValidator)
                .validarDadosEdicao(request);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.editar(1L, request));
        verifyNoInteractions(usuarioAutenticadoService, buscarItemCarrinhoService, itemCarrinhoRepository);
    }

    @Test
    @DisplayName("Deve propagar AccessDeniedException quando item pertencer a outro comprador")
    void devePropagarAccessDeniedExceptionQuandoItemPertencerAOutroComprador() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(1L)).thenReturn(item);
        doThrow(new AccessDeniedException("Você não possui permissão para acessar este item do carrinho"))
                .when(proprietarioItemCarrinhoValidatorService)
                .validarProprietario(item, comprador);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> service.editar(1L, request));
        verifyNoInteractions(validarDisponibilidadeItemValidatorService, itemCarrinhoRepository,
                editarItemCarrinhoMapper);
    }

    @Test
    @DisplayName("Deve propagar NegocioException quando nova quantidade exceder estoque ou anúncio estiver indisponível")
    void devePropagarNegocioExceptionQuandoEstoqueForExcedido() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequest.builder()
                .quantidade(10)
                .build();
        Usuario comprador = UsuarioFactory.get();
        ItemCarrinho item = ItemCarrinhoFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(1L)).thenReturn(item);
        doThrow(new NegocioException("Quantidade solicitada excede o estoque disponível do anúncio"))
                .when(validarDisponibilidadeItemValidatorService)
                .validar(item.getAnuncio(), comprador, 10);

        // Act & Assert
        assertThrows(NegocioException.class, () -> service.editar(1L, request));
        verify(itemCarrinhoRepository, never()).save(any());
        verifyNoInteractions(editarItemCarrinhoMapper);
    }

    @Test
    @DisplayName("Deve propagar RecursoNaoEncontradoException quando item do carrinho não existir")
    void devePropagarRecursoNaoEncontradoExceptionQuandoItemNaoExistir() {
        // Arrange
        EditarItemCarrinhoRequest request = EditarItemCarrinhoRequestFactory.get();
        Usuario comprador = UsuarioFactory.get();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(buscarItemCarrinhoService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Item do carrinho não encontrado"));

        // Act & Assert
        assertThrows(RecursoNaoEncontradoException.class, () -> service.editar(99L, request));
        verifyNoInteractions(proprietarioItemCarrinhoValidatorService, validarDisponibilidadeItemValidatorService,
                itemCarrinhoRepository, editarItemCarrinhoMapper);
    }
}
