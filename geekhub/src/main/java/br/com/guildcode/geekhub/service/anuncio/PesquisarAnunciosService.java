package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.enums.CondicaoProduto;
import br.com.guildcode.geekhub.domain.anuncio.enums.ConservacaoProduto;
import br.com.guildcode.geekhub.mapper.anuncio.PesquisarAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.validator.anuncio.PesquisarAnunciosValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Service
public class PesquisarAnunciosService {

    private final PesquisarAnunciosValidator pesquisarAnunciosValidator;
    private final AnuncioRepository anuncioRepository;
    private final PesquisarAnuncioMapper pesquisarAnuncioMapper;

    public PesquisarAnunciosService(
            PesquisarAnunciosValidator pesquisarAnunciosValidator,
            AnuncioRepository anuncioRepository,
            PesquisarAnuncioMapper pesquisarAnuncioMapper) {

        this.pesquisarAnunciosValidator = pesquisarAnunciosValidator;
        this.anuncioRepository = anuncioRepository;
        this.pesquisarAnuncioMapper = pesquisarAnuncioMapper;
    }

    @Transactional(readOnly = true)
    public Page<ListarAnuncioResponse> pesquisar(
            String nome,
            Long categoriaId,
            BigDecimal precoMin,
            BigDecimal precoMax,
            CondicaoProduto condicao,
            ConservacaoProduto conservacao,
            Long vendedorId,
            Pageable pageable) {

        pesquisarAnunciosValidator.validarFaixaPreco(precoMin, precoMax);

        String nomeFiltro = normalizarNome(nome);

        return anuncioRepository.pesquisarCatalogo(
                        nomeFiltro, categoriaId, precoMin, precoMax, condicao, conservacao, vendedorId, pageable)
                .map(pesquisarAnuncioMapper::toResponse);
    }

    private String normalizarNome(String nome) {

        if (isNull(nome) || nome.isBlank()) {
            return null;
        }

        return nome.trim();
    }
}
