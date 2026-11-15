package br.com.guildcode.geekhub.service.anuncio;

import br.com.guildcode.geekhub.controller.anuncio.dto.response.ListarMeuAnuncioResponse;
import br.com.guildcode.geekhub.domain.anuncio.Anuncio;
import br.com.guildcode.geekhub.domain.usuario.Usuario;
import br.com.guildcode.geekhub.factory.anuncio.AnuncioFactory;
import br.com.guildcode.geekhub.factory.usuario.UsuarioFactory;
import br.com.guildcode.geekhub.mapper.anuncio.ListarMeuAnuncioMapper;
import br.com.guildcode.geekhub.repository.anuncio.AnuncioRepository;
import br.com.guildcode.geekhub.service.auth.UsuarioAutenticadoService;
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

import static java.lang.Boolean.FALSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarMeusAnunciosServiceTest {

    @InjectMocks
    private ListarMeusAnunciosService service;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private ListarMeuAnuncioMapper listarMeuAnuncioMapper;

    @Test
    @DisplayName("Deve listar todos os anúncios do vendedor autenticado de forma paginada")
    void deveListarMeusAnunciosComSucesso() {

        // Arrange
        Usuario vendedor = UsuarioFactory.get();
        Anuncio anuncioInativo = AnuncioFactory.get();
        anuncioInativo.setDisponivel(FALSE);
        anuncioInativo.setQuantidadeDisponivel(0);

        ListarMeuAnuncioResponse responseItem = ListarMeuAnuncioResponse.builder()
                .id(anuncioInativo.getId())
                .nome(anuncioInativo.getNome())
                .disponivel(FALSE)
                .quantidadeDisponivel(0)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Anuncio> anuncios = new PageImpl<>(List.of(anuncioInativo), pageable, 1);

        when(usuarioAutenticadoService.get()).thenReturn(vendedor);
        when(anuncioRepository.findByVendedorId(vendedor.getId(), pageable)).thenReturn(anuncios);
        when(listarMeuAnuncioMapper.toResponse(anuncioInativo)).thenReturn(responseItem);

        // Act
        Page<ListarMeuAnuncioResponse> pagina = service.listar(pageable);

        // Assert
        assertEquals(1, pagina.getTotalElements());
        assertEquals(responseItem, pagina.getContent().getFirst());
        verify(usuarioAutenticadoService).get();
        verify(anuncioRepository).findByVendedorId(vendedor.getId(), pageable);
        verify(listarMeuAnuncioMapper).toResponse(anuncioInativo);
    }
}
