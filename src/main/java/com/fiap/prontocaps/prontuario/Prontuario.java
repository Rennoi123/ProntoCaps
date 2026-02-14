package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.paciente.Paciente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Prontuario")
@Getter
@Setter
@NoArgsConstructor
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "profissional_username", nullable = false)
    private String profissionalUsername;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false)
    private Integer versao;

    @Column(name = "classificacao_risco")
    private String classificacaoRisco;

    @Column(name = "assinatura_digital")
    private String assinaturaDigital;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false)
    private Boolean atual;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}