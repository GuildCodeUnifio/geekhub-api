package br.com.guildcode.geekhub.controller.auth.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        Long expiresIn
) {}
