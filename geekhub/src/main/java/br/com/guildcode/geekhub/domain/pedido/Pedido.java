package br.com.guildcode.geekhub.domain.pedido;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido.CONCLUIDO;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Objects.isNull;
import static org.hibernate.type.SqlTypes.VARCHAR;

@Entity
@Table(name = "pedido")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Pedido {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comprador_id", nullable = false)
    private Usuario comprador;

    @Column(name = "comprador_nome", nullable = false, length = 255)
    private String compradorNome;

    @Column(name = "comprador_email", nullable = false, length = 255)
    private String compradorEmail;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(STRING)
    @JdbcTypeCode(VARCHAR)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;

    @Column(nullable = false)
    private Boolean avaliado;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "pedido_item", joinColumns = @JoinColumn(name = "pedido_id"))
    private List<ItemPedido> itens = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "pedido_evento", joinColumns = @JoinColumn(name = "pedido_id"))
    @OrderColumn(name = "ordem")
    private List<EventoPedido> historico = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public void calcularValorTotal() {

        this.valorTotal = this.itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(ZERO, BigDecimal::add)
                .setScale(2, HALF_UP);
    }

    public void registrarEvento(EstadoPedido novoEstado, LocalDateTime ocorridoEm) {

        this.estado = novoEstado;
        this.historico.add(EventoPedido.builder()
                .estado(novoEstado)
                .ocorridoEm(ocorridoEm)
                .build());
    }

    public Integer calcularQuantidadeItens() {

        return this.itens.stream()
                .map(ItemPedido::getQuantidade)
                .reduce(0, Integer::sum);
    }

    public Boolean pertenceAoComprador(Long usuarioId) {

        return this.comprador.getId().equals(usuarioId);
    }

    public Boolean possuiVendedor(Long vendedorId) {

        return this.itens.stream()
                .anyMatch(item -> item.getVendedorId().equals(vendedorId));
    }

    public Boolean isConcluido() {

        return CONCLUIDO.equals(this.estado);
    }

    public Long obterVendedorId() {

        if (isNull(this.itens) || this.itens.isEmpty()) {
            return null;
        }

        return this.itens.getFirst().getVendedorId();
    }
}
