package com.fiap.prontocaps.paciente.dto;

import java.time.LocalDate;

public record PacienteResponse(
    Long id,
    String nome,
    String cpf,
    LocalDate dataNascimento,
    boolean ativo
) {}
