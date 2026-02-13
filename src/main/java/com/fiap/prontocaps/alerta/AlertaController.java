package com.fiap.prontocaps.alerta;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaRepository repository;

    public AlertaController(AlertaRepository repository) {
        this.repository = repository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTENTE_SOCIAL')")
    @GetMapping("/pendentes")
    public ResponseEntity<List<Alerta>> listarPendentes() {
        return ResponseEntity.ok(repository.findByStatus("PENDENTE"));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ASSISTENTE_SOCIAL')")
    @PutMapping("/{id}/resolver")
    public ResponseEntity<Void> marcarComoResolvido(@PathVariable Long id) {
        repository.findById(id).ifPresent(alerta -> {
            alerta.setStatus("RESOLVIDO");
            repository.save(alerta);
        });
        return ResponseEntity.noContent().build();
    }
}