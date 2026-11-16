package br.com.guildcode.geekhub.mapper.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.CadastrarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import br.com.guildcode.geekhub.factory.avaliacao.CadastrarAvaliacaoRequestFactory;
import br.com.guildcode.geekhub.factory.pedido.PedidoFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CadastrarAvaliacaoMapperTest {

    private final CadastrarAvaliacaoMapper mapper = new CadastrarAvaliacaoMapper();

    @Test
    @DisplayName("Deve converter CadastrarAvaliacaoRequest para entidade Avaliacao")
    void deveConverterRequestParaEntidade() {
        // Arrange
        CadastrarAvaliacaoRequest request = CadastrarAvaliacaoRequestFactory.get();
        Pedido pedido = PedidoFactory.get();
        Usuario autor = pedido.getComprador();
        Usuario vendedor = UsuarioFactory.get();
        vendedor.setId(2L);
        LocalDateTime agora = LocalDateTime.of(2026, 11, 15, 21, 0);

        // Act
        Avaliacao avaliacao = mapper.toEntity(request, pedido, autor, vendedor, agora);

        // Assert
        assertEquals(pedido, avaliacao.getPedido());
        assertEquals(autor, avaliacao.getAutor());
        assertEquals(vendedor, avaliacao.getVendedor());
        assertEquals(request.nota(), avaliacao.getNota());
        assertEquals(request.comentario(), avaliacao.getComentario());
        assertEquals(agora, avaliacao.getCriadoEm());
    }

    @Test
    @DisplayName("Deve converter entidade Avaliacao para CadastrarAvaliacaoResponse")
    void deveConverterEntidadeParaResponse() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();

        // Act
        CadastrarAvaliacaoResponse response = mapper.toResponse(avaliacao);

        // Assert
        assertEquals(avaliacao.getId(), response.id());
        assertEquals(avaliacao.getPedido().getId(), response.pedidoId());
        assertEquals(avaliacao.getVendedor().getId(), response.vendedorId());
        assertEquals(avaliacao.getAutor().getId(), response.autorId());
        assertEquals(avaliacao.getAutor().getNome(), response.autorNome());
        assertEquals(avaliacao.getNota(), response.nota());
        assertEquals(avaliacao.getComentario(), response.comentario());
        assertEquals(avaliacao.getCriadoEm(), response.criadoEm());
    }
}
