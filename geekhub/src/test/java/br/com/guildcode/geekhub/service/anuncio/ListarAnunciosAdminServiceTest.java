package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarAnuncioAdminResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.mapper.anuncio.ListarAnuncioAdminMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarAnunciosAdminServiceTest {

    @InjectMocks
    private ListarAnunciosAdminService service;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private ListarAnuncioAdminMapper listarAnuncioAdminMapper;

    @Test
    @DisplayName("Deve listar todos os anúncios da plataforma para administração de forma paginada")
    void deveListarTodosAnunciosParaAdmin() {

        // Arrange
        Anuncio anuncio = AnuncioFactory.get();
        ListarAnuncioAdminResponse item = ListarAnuncioAdminResponse.builder()
                .id(anuncio.getId())
                .nome(anuncio.getNome())
                .vendedorId(anuncio.getVendedor().getId())
                .vendedorNome(anuncio.getVendedor().getNome())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Anuncio> anuncios = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.findAll(pageable)).thenReturn(anuncios);
        when(listarAnuncioAdminMapper.toResponse(anuncio)).thenReturn(item);

        // Act
        Page<ListarAnuncioAdminResponse> pagina = service.listar(pageable);

        // Assert
        assertEquals(1, pagina.getTotalElements());
        assertEquals(item, pagina.getContent().getFirst());
        verify(anuncioRepository).findAll(pageable);
        verify(listarAnuncioAdminMapper).toResponse(anuncio);
    }
}
