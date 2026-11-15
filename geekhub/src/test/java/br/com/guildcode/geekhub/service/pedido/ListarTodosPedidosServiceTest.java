package br.com.guildcode.geekhub.service.pedido;

import br.com.guildcode.geekhub.controller.pedido.dto.response.ListarPedidoAdminResponse;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.mapper.pedido.ListarPedidoAdminMapper;
import br.com.guildcode.geekhub.repository.pedido.PedidoRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarTodosPedidosServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ListarPedidoAdminMapper listarPedidoAdminMapper;

    @InjectMocks
    private ListarTodosPedidosService service;

    @Test
    @DisplayName("Deve listar todos os pedidos paginados para administrador")
    void deveListarTodosPedidosPaginados() {
        // Arrange
        Pedido pedido = PedidoFactory.get();
        Pageable pageable = PageRequest.of(0, 10);
        ListarPedidoAdminResponse itemResponse = ListarPedidoAdminResponse.builder()
                .id(pedido.getId())
                .compradorId(pedido.getComprador().getId())
                .compradorNome(pedido.getCompradorNome())
                .compradorEmail(pedido.getCompradorEmail())
                .valorTotal(pedido.getValorTotal())
                .estado(pedido.getEstado())
                .avaliado(pedido.getAvaliado())
                .quantidadeItens(1)
                .criadoEm(pedido.getCriadoEm())
                .build();

        when(pedidoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(pedido)));
        when(listarPedidoAdminMapper.toResponse(pedido)).thenReturn(itemResponse);

        // Act
        Page<ListarPedidoAdminResponse> resultado = service.listar(pageable);

        // Assert
        assertEquals(1, resultado.getTotalElements());
        assertEquals(itemResponse, resultado.getContent().getFirst());
        verify(pedidoRepository).findAll(pageable);
    }
}
