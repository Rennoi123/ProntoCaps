package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.prontuario.dto.ProntuarioRequest;
import com.fiap.prontocaps.prontuario.dto.ProntuarioResponse;
import jakarta.transaction.UserTransaction;
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

    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI')")
    @PostMapping("/pacientes/{pacienteId}/prontuarios")
    public ResponseEntity<ProntuarioResponse> criarPrimeiro(
      @PathVariable("pacienteId") Long pacienteId,
      @RequestBody @Valid ProntuarioRequest prontuarioRequest,
      Authentication auth) {
        return ResponseEntity.ok(service.criarPrimeiro(pacienteId, prontuarioRequest, auth.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI')")
    @PostMapping("/pacientes/{pacienteId}/prontuarios/nova-versao")
    public ResponseEntity<ProntuarioResponse> criarNovaVersao(
            @PathVariable("pacienteId") Long pacienteId,
            @RequestBody @Valid ProntuarioRequest prontuarioRequest,
            Authentication auth) {
        return ResponseEntity.ok(service.criarNovaVersao(pacienteId, prontuarioRequest.descricao(), auth.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MEDICO','PSI','ENFERMEIRO','ASSISTENTE_SOCIAL')")
    @GetMapping("/pacientes/{pacienteId}/prontuarios")
    public ResponseEntity<List<ProntuarioResponse>> listar(@PathVariable("pacienteId") Long pacienteId) {
        return ResponseEntity.ok(service.listByPaciente(pacienteId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/prontuarios/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable("id") Long id) {
        service.inativar(id);
        return ResponseEntity.ok().build();
    }
}
