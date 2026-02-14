package com.fiap.prontocaps.prontuario.dto;

import java.time.LocalDateTime;

public record ProntuarioResponse(
    Long id,
    Long pacienteId,
    LocalDateTime dataRegistro,
    String profissionalUsername,
    String descricao,
    String classificacaoRisco,
    Integer versao,
    boolean versaoAtual,
    boolean prontuarioAtivo,
    String assinaturaDigital
) {}
