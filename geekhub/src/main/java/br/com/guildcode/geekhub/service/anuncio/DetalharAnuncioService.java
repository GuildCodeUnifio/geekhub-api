package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.DetalharAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.mapper.anuncio.DetalharAnuncioMapper;
import br.com.guildcode.geekhub.repository.avaliacao.AvaliacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Objects.nonNull;

@Service
public class DetalharAnuncioService {

    private final BuscarAnuncioService buscarAnuncioService;
    private final AvaliacaoRepository avaliacaoRepository;
    private final DetalharAnuncioMapper detalharAnuncioMapper;

    public DetalharAnuncioService(
            BuscarAnuncioService buscarAnuncioService,
            AvaliacaoRepository avaliacaoRepository,
            DetalharAnuncioMapper detalharAnuncioMapper) {

        this.buscarAnuncioService = buscarAnuncioService;
        this.avaliacaoRepository = avaliacaoRepository;
        this.detalharAnuncioMapper = detalharAnuncioMapper;
    }

    @Transactional(readOnly = true)
    public DetalharAnuncioResponse detalhar(Long id) {

        Anuncio anuncio = buscarAnuncioService.buscarPorId(id);
        Long vendedorId = anuncio.getVendedor().getId();

        Double notaMediaBruta = avaliacaoRepository.calcularNotaMediaPorVendedorId(vendedorId);
        Double notaMedia = nonNull(notaMediaBruta)
                ? BigDecimal.valueOf(notaMediaBruta).setScale(2, HALF_UP).doubleValue()
                : null;
        Long quantidadeAvaliacoes = avaliacaoRepository.contarPorVendedorId(vendedorId);

        return detalharAnuncioMapper.toResponse(anuncio, notaMedia, quantidadeAvaliacoes);
    }
}
