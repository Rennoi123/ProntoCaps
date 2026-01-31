package com.fiap.prontocaps.prontuario.dto;

import jakarta.validation.constraints.NotBlank;

public record ProntuarioRequest(
    @NotBlank String descricao
) {}
