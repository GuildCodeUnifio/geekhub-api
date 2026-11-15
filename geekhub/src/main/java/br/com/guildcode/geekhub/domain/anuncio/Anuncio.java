package br.com.guildcode.geekhub.domain.anuncio;

import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import br.com.guildcode.geekhub.domain.categoria.Categoria;
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

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.isNull;
import static org.hibernate.type.SqlTypes.VARCHAR;

@Entity
@Table(name = "anuncio")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
public class Anuncio {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @Column(nullable = false)
    private Boolean disponivel;

    @Enumerated(STRING)
    @JdbcTypeCode(VARCHAR)
    @Column(nullable = false, length = 30)
    private CondicaoProduto condicao;

    @Enumerated(STRING)
    @JdbcTypeCode(VARCHAR)
    @Column(nullable = false, length = 30)
    private ConservacaoProduto conservacao;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "vendedor_id", nullable = false)
    private Usuario vendedor;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "anuncio_imagem", joinColumns = @JoinColumn(name = "anuncio_id"))
    @OrderColumn(name = "ordem")
    @Column(name = "url_imagem", nullable = false, length = 512)
    private List<String> imagens = new ArrayList<>();

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    public String obterImagemPrincipal() {

        if (isNull(this.imagens) || this.imagens.isEmpty()) {
            return null;
        }

        return this.imagens.getFirst();
    }
}
