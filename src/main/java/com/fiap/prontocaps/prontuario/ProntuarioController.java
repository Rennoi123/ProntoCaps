package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.prontuario.dto.ProntuarioRequest;
import com.fiap.prontocaps.prontuario.dto.ProntuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProntuarioController {

  private final ProntuarioService service;

  public ProntuarioController(ProntuarioService service) {
    this.service = service;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI','ENFERMEIRO','ASSISTENTE_SOCIAL')")
  @PostMapping("/pacientes/{pacienteId}/prontuarios")
  public ResponseEntity<ProntuarioResponse> create(
      @PathVariable Long pacienteId,
      @RequestBody @Valid ProntuarioRequest req,
      Authentication auth
  ) {
    return ResponseEntity.ok(service.create(pacienteId, req, auth.getName()));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI','ENFERMEIRO','ASSISTENTE_SOCIAL')")
  @GetMapping("/pacientes/{pacienteId}/prontuarios")
  public ResponseEntity<List<ProntuarioResponse>> list(@PathVariable Long pacienteId) {
    return ResponseEntity.ok(service.listByPaciente(pacienteId));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/prontuarios/{id}")
  public ResponseEntity<Void> inactivate(@PathVariable Long id) {
    service.inactivate(id);
    return ResponseEntity.noContent().build();
  }
}
