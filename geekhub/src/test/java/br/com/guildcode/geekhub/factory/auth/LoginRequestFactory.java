package br.com.guildcode.geekhub.factory.auth;

import br.com.guildcode.geekhub.controller.auth.request.LoginRequest;

public class LoginRequestFactory {

    public static LoginRequest get() {

        return LoginRequest.builder()
                .email("rafael@email.com")
                .senha("123456")
                .build();
    }
}
