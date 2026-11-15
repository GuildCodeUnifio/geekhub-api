package br.com.guildcode.geekhub.domain.pedido;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemPedido {

    @Column(name = "anuncio_id", nullable = false)
    private Long anuncioId;

    @Column(name = "vendedor_id", nullable = false)
    private Long vendedorId;

    @Column(name = "nome_produto", nullable = false, length = 255)
    private String nomeProduto;

    @Column(name = "imagem_produto", length = 512)
    private String imagemProduto;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
