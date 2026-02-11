package com.fiap.prontocaps.prontuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {
    List<Prontuario> findByPacienteIdOrderByVersaoDesc(Long pacienteId);
    Optional<Prontuario> findByPacienteIdAndAtualTrue(Long pacienteId);
}
