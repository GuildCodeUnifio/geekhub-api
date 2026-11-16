package br.com.guildcode.geekhub.mapper.avaliacao;

import br.com.guildcode.geekhub.controller.avaliacao.dto.request.CadastrarAvaliacaoRequest;
import br.com.guildcode.geekhub.controller.avaliacao.dto.response.CadastrarAvaliacaoResponse;
import br.com.guildcode.geekhub.domain.avaliacao.Avaliacao;
import br.com.guildcode.geekhub.domain.pedido.Pedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CadastrarAvaliacaoMapper {

    public Avaliacao toEntity(
            CadastrarAvaliacaoRequest request,
            Pedido pedido,
            Usuario autor,
            Usuario vendedor,
            LocalDateTime agora) {

        return Avaliacao.builder()
                .pedido(pedido)
                .autor(autor)
                .vendedor(vendedor)
                .nota(request.nota())
                .comentario(request.comentario())
                .criadoEm(agora)
                .build();
    }

    public CadastrarAvaliacaoResponse toResponse(Avaliacao entity) {

        return CadastrarAvaliacaoResponse.builder()
                .id(entity.getId())
                .pedidoId(entity.getPedido().getId())
                .vendedorId(entity.getVendedor().getId())
                .autorId(entity.getAutor().getId())
                .autorNome(entity.getAutor().getNome())
                .nota(entity.getNota())
                .comentario(entity.getComentario())
                .criadoEm(entity.getCriadoEm())
                .build();
    }
}
