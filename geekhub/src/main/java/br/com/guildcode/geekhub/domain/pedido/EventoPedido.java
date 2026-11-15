package br.com.guildcode.geekhub.domain.pedido;

import br.com.guildcode.geekhub.domain.pedido.enums.EstadoPedido;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static org.hibernate.type.SqlTypes.VARCHAR;

@Embeddable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EventoPedido {

    @Enumerated(STRING)
    @JdbcTypeCode(VARCHAR)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;

    @Column(name = "ocorrido_em", nullable = false)
    private LocalDateTime ocorridoEm;
}
