package br.com.guildcode.geekhub.domain.carrinho;

import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.math.RoundingMode.HALF_UP;

@Entity
@Table(
        name = "item_carrinho",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_item_carrinho_comprador_anuncio",
                columnNames = {"comprador_id", "anuncio_id"}
        )
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @ManyToOne
    @JoinColumn(name = "anuncio_id", nullable = false)
    private Anuncio anuncio;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public BigDecimal calcularSubtotal() {

        return this.anuncio.getPreco()
                .multiply(BigDecimal.valueOf(this.quantidade))
                .setScale(2, HALF_UP);
    }
}
