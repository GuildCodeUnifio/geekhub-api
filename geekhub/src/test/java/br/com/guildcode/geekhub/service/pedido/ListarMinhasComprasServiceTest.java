package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarCompraResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.pedido.ListarCompraMapper;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
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

import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarMinhasComprasServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ListarCompraMapper listarCompraMapper;

    @InjectMocks
    private ListarMinhasComprasService service;

    @Test
    @DisplayName("Deve listar todas as compras do comprador quando filtro de estado não for informado")
    void deveListarTodasComprasSemFiltroDeEstado() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        Pedido pedido = PedidoFactory.get();
        Pageable pageable = PageRequest.of(0, 10);
        ListarCompraResponse itemResponse = ListarCompraResponse.builder()
                .id(pedido.getId())
                .valorTotal(pedido.getValorTotal())
                .estado(pedido.getEstado())
                .avaliado(pedido.getAvaliado())
                .quantidadeItens(1)
                .criadoEm(pedido.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(pedidoRepository.findByCompradorId(comprador.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(pedido)));
        when(listarCompraMapper.toResponse(pedido)).thenReturn(itemResponse);

        // Act
        Page<ListarCompraResponse> resultado = service.listar(null, pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(itemResponse, resultado.getContent().getFirst());
        verify(pedidoRepository).findByCompradorId(comprador.getId(), pageable);
        verify(pedidoRepository, never()).findByCompradorIdAndEstado(any(), any(), any());
    }

    @Test
    @DisplayName("Deve listar compras do comprador filtradas por estado quando filtro for informado")
    void deveListarComprasFiltradasPorEstado() {
        // Arrange
        Usuario comprador = UsuarioFactory.get();
        Pedido pedido = PedidoFactory.get();
        pedido.setEstado(CONCLUIDO);
        Pageable pageable = PageRequest.of(0, 10);
        ListarCompraResponse itemResponse = ListarCompraResponse.builder()
                .id(pedido.getId())
                .valorTotal(pedido.getValorTotal())
                .estado(CONCLUIDO)
                .avaliado(pedido.getAvaliado())
                .quantidadeItens(1)
                .criadoEm(pedido.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(comprador);
        when(pedidoRepository.findByCompradorIdAndEstado(comprador.getId(), CONCLUIDO, pageable))
                .thenReturn(new PageImpl<>(List.of(pedido)));
        when(listarCompraMapper.toResponse(pedido)).thenReturn(itemResponse);

        // Act
        Page<ListarCompraResponse> resultado = service.listar(CONCLUIDO, pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(CONCLUIDO, resultado.getContent().getFirst().estado());
        verify(pedidoRepository).findByCompradorIdAndEstado(comprador.getId(), CONCLUIDO, pageable);
        verify(pedidoRepository, never()).findByCompradorId(any(), any());
    }
}
