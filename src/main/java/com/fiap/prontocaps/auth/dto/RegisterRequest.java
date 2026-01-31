package com.fiap.prontocaps.auth.dto;

import com.fiap.prontocaps.user.Roles;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @NotBlank String username,
    @NotBlank String password,
    @NotNull Roles roles
) {}
