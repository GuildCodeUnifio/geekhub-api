package br.com.guildcode.geekhub.controller.auth;

import br.com.guildcode.geekhub.controller.auth.request.LoginRequest;
import br.com.guildcode.geekhub.controller.auth.response.LoginResponse;
import br.com.guildcode.geekhub.openapi.auth.LoginControllerOpenApi;
import br.com.guildcode.geekhub.service.auth.LoginService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/login")
public class LoginController implements LoginControllerOpenApi {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {

        this.loginService = loginService;
    }

    @PostMapping
    @ResponseStatus(OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {

        return loginService.login(loginRequest);
    }
}
