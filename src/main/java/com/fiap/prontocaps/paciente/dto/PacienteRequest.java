package com.fiap.prontocaps.paciente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,
        @NotBlank(message = "CPF é obrigatório")
        @CPF String cpf,
        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDate dataNascimento
) {}
