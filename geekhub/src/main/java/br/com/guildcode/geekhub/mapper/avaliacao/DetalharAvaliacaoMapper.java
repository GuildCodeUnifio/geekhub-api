package br.com.guildcode.geekhub.mapper.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.response.AutorAvaliacaoResponse;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.DetalharAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

@Component
public class DetalharAvaliacaoMapper {

    public DetalharAvaliacaoResponse toResponse(Avaliacao entity) {

        Usuario autor = entity.getAutor();

        AutorAvaliacaoResponse autorResponse = AutorAvaliacaoResponse.builder()
                .id(autor.getId())
                .nome(autor.getNome())
                .imagemPerfil(autor.getImagemPerfil())
                .build();

        return DetalharAvaliacaoResponse.builder()
                .id(entity.getId())
                .pedidoId(entity.getPedido().getId())
                .vendedorId(entity.getVendedor().getId())
                .autor(autorResponse)
                .nota(entity.getNota())
                .comentario(entity.getComentario())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
