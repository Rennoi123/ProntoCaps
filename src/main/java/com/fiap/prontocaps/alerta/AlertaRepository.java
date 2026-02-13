package com.fiap.prontocaps.alerta;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByStatus(String status);
    boolean existsByPacienteIdAndStatus(Long pacienteId, String status);
    List<Alerta> findByPacienteIdAndStatus(Long pacienteId, String status);
}