package com.fiap.prontocaps.paciente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PacienteRequest(
    @NotBlank String nome,
    @NotBlank String cpf,
    @NotNull LocalDate dataNascimento
) {}
