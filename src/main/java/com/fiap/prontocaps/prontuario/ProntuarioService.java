package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.common.NotFoundException;
import com.fiap.prontocaps.paciente.PacienteRepository;
import com.fiap.prontocaps.prontuario.dto.ProntuarioRequest;
import com.fiap.prontocaps.prontuario.dto.ProntuarioResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProntuarioService {

  private final ProntuarioRepository repo;
  private final PacienteRepository pacienteRepo;

  public ProntuarioService(ProntuarioRepository repo, PacienteRepository pacienteRepo) {
    this.repo = repo;
    this.pacienteRepo = pacienteRepo;
  }

  public ProntuarioResponse create(Long pacienteId, ProntuarioRequest req, String profissionalUsername) {
    var paciente = pacienteRepo.findById(pacienteId)
        .orElseThrow(() -> new NotFoundException("Paciente nao encontrado"));

    Prontuario p = new Prontuario();
    p.setPaciente(paciente);
    p.setDescricao(req.descricao());
    p.setProfissionalUsername(profissionalUsername);
    p.setDataRegistro(LocalDateTime.now());
    repo.save(p);

    return toResponse(p);
  }

  public List<ProntuarioResponse> listByPaciente(Long pacienteId) {
    pacienteRepo.findById(pacienteId).orElseThrow(() -> new NotFoundException("Paciente nao encontrado"));
    return repo.findByPacienteIdOrderByDataRegistroDesc(pacienteId)
        .stream().map(this::toResponse).toList();
  }

  public void inactivate(Long prontuarioId) {
    Prontuario p = repo.findById(prontuarioId)
        .orElseThrow(() -> new NotFoundException("Prontuario nao encontrado"));
    p.setAtivo(false);
    repo.save(p);
  }

  private ProntuarioResponse toResponse(Prontuario p) {
    return new ProntuarioResponse(
        p.getId(),
        p.getPaciente().getId(),
        p.getDataRegistro(),
        p.getProfissionalUsername(),
        p.getDescricao(),
        p.isAtivo()
    );
  }
}
