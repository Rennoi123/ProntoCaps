package com.fiap.prontocaps.paciente;

import com.fiap.prontocaps.paciente.dto.PacienteRequest;
import com.fiap.prontocaps.paciente.dto.PacienteResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

  private final PacienteService service;

  public PacienteController(PacienteService service) {
    this.service = service;
  }

  @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI','ENFERMEIRO','ASSISTENTE_SOCIAL')")
  @PostMapping
  public ResponseEntity<PacienteResponse> create(@RequestBody @Valid PacienteRequest req) {
    return ResponseEntity.ok(service.criar(req));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI','ENFERMEIRO','ASSISTENTE_SOCIAL')")
  @GetMapping("/{id}")
  public ResponseEntity<PacienteResponse> get(@PathVariable("id") Long id) {
    return ResponseEntity.ok(service.buscarPorId(id));
  }

  @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI','ENFERMEIRO','ASSISTENTE_SOCIAL')")
  @GetMapping
  public ResponseEntity<List<PacienteResponse>> list() {
    return ResponseEntity.ok(service.list());
  }

  @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI','ENFERMEIRO')")
  @PutMapping("/{id}")
  public ResponseEntity<PacienteResponse> update(@PathVariable("id") Long id, @RequestBody @Valid PacienteRequest req) {
    return ResponseEntity.ok(service.update(id, req));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> inactivate(@PathVariable("id") Long id) {
    service.inativar(id);
    return ResponseEntity.noContent().build();
  }
}
