package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarVendaResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.pedido.ListarVendaMapper;
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

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.ENVIADO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarMinhasVendasServiceTest {

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ListarVendaMapper listarVendaMapper;

    @InjectMocks
    private ListarMinhasVendasService service;

    @Test
    @DisplayName("Deve listar vendas do vendedor sem filtro de estado")
    void deveListarVendasSemFiltroDeEstado() {
        // Arrange
        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);
        Pedido pedido = PedidoFactory.get();
        Pageable pageable = PageRequest.of(0, 10);
        ListarVendaResponse itemResponse = ListarVendaResponse.builder()
                .id(pedido.getId())
                .compradorNome(pedido.getCompradorNome())
                .valorTotal(pedido.getValorTotal())
                .estado(pedido.getEstado())
                .criadoEm(pedido.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(vendedor);
        when(pedidoRepository.listarVendas(2L, null, pageable))
                .thenReturn(new PageImpl<>(List.of(pedido)));
        when(listarVendaMapper.toResponse(pedido)).thenReturn(itemResponse);

        // Act
        Page<ListarVendaResponse> resultado = service.listar(null, pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(itemResponse, resultado.getContent().getFirst());
        verify(pedidoRepository).listarVendas(2L, null, pageable);
    }

    @Test
    @DisplayName("Deve repassar filtro opcional de estado na consulta de vendas do vendedor")
    void deveListarVendasFiltrandoPorEstado() {
        // Arrange
        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);
        Pedido pedido = PedidoFactory.get();
        pedido.setEstado(ENVIADO);
        Pageable pageable = PageRequest.of(0, 10);
        ListarVendaResponse itemResponse = ListarVendaResponse.builder()
                .id(pedido.getId())
                .compradorNome(pedido.getCompradorNome())
                .valorTotal(pedido.getValorTotal())
                .estado(ENVIADO)
                .criadoEm(pedido.getCriadoEm())
                .build();

        when(usuarioAutenticadoService.get()).thenReturn(vendedor);
        when(pedidoRepository.listarVendas(2L, ENVIADO, pageable))
                .thenReturn(new PageImpl<>(List.of(pedido)));
        when(listarVendaMapper.toResponse(pedido)).thenReturn(itemResponse);

        // Act
        Page<ListarVendaResponse> resultado = service.listar(ENVIADO, pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(ENVIADO, resultado.getContent().getFirst().estado());
        verify(pedidoRepository).listarVendas(2L, ENVIADO, pageable);
    }
}
