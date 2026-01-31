package com.fiap.prontocaps.paciente;

import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.common.NotFoundException;
import com.fiap.prontocaps.paciente.dto.PacienteRequest;
import com.fiap.prontocaps.paciente.dto.PacienteResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

  private final PacienteRepository repo;

  public PacienteService(PacienteRepository repo) {
    this.repo = repo;
  }

  public PacienteResponse create(PacienteRequest req) {
    if (repo.findByCpf(req.cpf()).isPresent()) {
      throw new BusinessException("CPF ja cadastrado");
    }
    Paciente p = new Paciente();
    p.setNome(req.nome());
    p.setCpf(req.cpf());
    p.setDataNascimento(req.dataNascimento());
    repo.save(p);
    return toResponse(p);
  }

  public PacienteResponse get(Long id) {
    Paciente p = repo.findById(id).orElseThrow(() -> new NotFoundException("Paciente nao encontrado"));
    return toResponse(p);
  }

  public List<PacienteResponse> list() {
    return repo.findAll().stream().map(this::toResponse).toList();
  }

  public PacienteResponse update(Long id, PacienteRequest req) {
    Paciente p = repo.findById(id).orElseThrow(() -> new NotFoundException("Paciente nao encontrado"));

    if (!p.getCpf().equals(req.cpf()) && repo.findByCpf(req.cpf()).isPresent()) {
      throw new BusinessException("CPF ja cadastrado");
    }

    p.setNome(req.nome());
    p.setCpf(req.cpf());
    p.setDataNascimento(req.dataNascimento());
    repo.save(p);
    return toResponse(p);
  }

  public void inactivate(Long id) {
    Paciente p = repo.findById(id).orElseThrow(() -> new NotFoundException("Paciente nao encontrado"));
    p.setAtivo(false);
    repo.save(p);
  }

  private PacienteResponse toResponse(Paciente p) {
    return new PacienteResponse(p.getId(), p.getNome(), p.getCpf(), p.getDataNascimento(), p.isAtivo());
  }
}
