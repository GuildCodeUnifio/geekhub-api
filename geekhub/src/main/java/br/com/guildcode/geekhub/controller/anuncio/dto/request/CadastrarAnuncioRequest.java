package br.com.guildcode.geekhub.controller.anuncio.dto.request;

import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CadastrarAnuncioRequest(

        @NotBlank(message = "é obrigatório")
        @Size(max = 255)
        String nome,

        @NotBlank(message = "é obrigatória")
        @Size(max = 2000)
        String descricao,

        @NotNull(message = "é obrigatória")
        @Positive(message = "deve ser positiva")
        Long categoriaId,

        @NotNull(message = "é obrigatório")
        @Positive(message = "deve ser maior que zero")
        @Digits(integer = 8, fraction = 2)
        BigDecimal preco,

        @NotNull(message = "é obrigatória")
        @Positive(message = "deve ser maior que zero")
        Integer quantidadeDisponivel,

        @NotNull(message = "é obrigatória")
        CondicaoProduto condicao,

        @NotNull(message = "é obrigatória")
        ConservacaoProduto conservacao,

        @NotEmpty(message = "deve conter ao menos uma imagem")
        List<@NotBlank(message = "é obrigatória") @Size(max = 512) @URL(message = "deve ser uma URL válida") String> imagens
) {}
