package br.com.guildcode.geekhub.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar 400 com mensagem do primeiro erro e lista de detalhes quando houver erros de validação")
    void deveTratarMethodArgumentNotValidExceptionComErrosDeCampo() {

        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("request", "email", "deve ser válido");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("deve ser válido", response.getBody().get("erro"));
        assertEquals(List.of("email: deve ser válido"), response.getBody().get("detalhes"));
    }

    @Test
    @DisplayName("Deve retornar 400 com mensagem padrão quando MethodArgumentNotValidException não tiver FieldErrors")
    void deveTratarMethodArgumentNotValidExceptionSemErrosDeCampo() {

        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(emptyList());

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro de validação nos campos.", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando NegocioException for lançada")
    void deveTratarNegocioException() {

        // Arrange
        NegocioException ex = new NegocioException("Regra de negócio violada");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleNegocio(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Regra de negócio violada", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando ConflitoException for lançada")
    void deveTratarConflitoException() {

        // Arrange
        ConflitoException ex = new ConflitoException("E-mail já cadastrado");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleConflito(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("E-mail já cadastrado", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 404 quando RecursoNaoEncontradoException for lançada")
    void deveTratarRecursoNaoEncontradoException() {

        // Arrange
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("Recurso não encontrado");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleRecursoNaoEncontrado(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Recurso não encontrado", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 409 quando DataIntegrityViolationException for lançada")
    void deveTratarDataIntegrityViolationException() {

        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate key");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrity(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Violação de integridade: registro duplicado ou dados inválidos.", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando HttpMessageNotReadableException for lançada")
    void deveTratarHttpMessageNotReadableException() {

        // Arrange
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleJsonError(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Corpo da requisição inválido ou malformatado.", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar o status e reason correspondentes quando ResponseStatusException for lançada")
    void deveTratarResponseStatusException() {

        // Arrange
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Entidade improcessável");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(ex);

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Entidade improcessável", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 401 com mensagem da exceção quando BadCredentialsException for lançada")
    void deveTratarBadCredentialsException() {

        // Arrange
        BadCredentialsException ex = new BadCredentialsException("Usuário ou senha incorretos");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleBadCredentials(ex);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Usuário ou senha incorretos", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 401 quando AuthenticationException genérica for lançada")
    void deveTratarAuthenticationException() {

        // Arrange
        AuthenticationException ex = new InsufficientAuthenticationException("Sem token");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAuthentication(ex);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado: Token ausente, malformado ou revogado.", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando AccessDeniedException for lançada")
    void deveTratarAccessDeniedException() {

        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Forbidden");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(ex);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado: você não possui permissão para realizar esta operação.", response.getBody().get("erro"));
    }

    @Test
    @DisplayName("Deve retornar 404 com caminho da rota quando NoResourceFoundException for lançada")
    void deveTratarNoResourceFoundException() {

        // Arrange
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, null, "rota-inexistente");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleRotaNaoEncontrada(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("A rota '/rota-inexistente' não existe na aplicação.", response.getBody().get("erro"));
    }
}
