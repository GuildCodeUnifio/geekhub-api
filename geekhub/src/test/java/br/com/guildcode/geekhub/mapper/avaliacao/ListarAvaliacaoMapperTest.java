package br.com.guildcode.geekhub.mapper.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.response.ListarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.factory.avaliacao.AvaliacaoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListarAvaliacaoMapperTest {

    private final ListarAvaliacaoMapper mapper = new ListarAvaliacaoMapper();

    @Test
    @DisplayName("Deve converter entidade Avaliacao para ListarAvaliacaoResponse")
    void deveConverterEntidadeParaResponse() {
        // Arrange
        Avaliacao avaliacao = AvaliacaoFactory.get();

        // Act
        ListarAvaliacaoResponse response = mapper.toResponse(avaliacao);

        // Assert
        assertEquals(avaliacao.getId(), response.id());
        assertEquals(avaliacao.getPedido().getId(), response.pedidoId());
        assertEquals(avaliacao.getVendedor().getId(), response.vendedorId());
        assertEquals(avaliacao.getAutor().getId(), response.autor().id());
        assertEquals(avaliacao.getAutor().getNome(), response.autor().nome());
        assertEquals(avaliacao.getAutor().getImagemPerfil(), response.autor().imagemPerfil());
        assertEquals(avaliacao.getNota(), response.nota());
        assertEquals(avaliacao.getComentario(), response.comentario());
        assertEquals(avaliacao.getCriadoEm(), response.criadoEm());
    }
}
