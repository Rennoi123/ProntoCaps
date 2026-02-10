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

    // --- LÓGICA DE INOVAÇÃO: TRIAGEM AUTOMÁTICA ---
    p.setClassificacaoRisco(calcularRisco(req.descricao()));
    // ----------------------------------------------

    repo.save(p);

    return toResponse(p);
  }

  // Algoritmo simples de triagem por palavras-chave
  private String calcularRisco(String texto) {
    if (texto == null) return "PADRAO";
    String t = texto.toLowerCase();

    // Palavras de ALTO RISCO (Exemplos para Saúde Mental/CAPS)
    if (t.contains("suicidio") || t.contains("suicídio") ||
            t.contains("agressivo") || t.contains("surto") ||
            t.contains("alucinação") || t.contains("risco de vida")) {
      return "ALTO_RISCO_VERMELHO";
    }

    // Palavras de MÉDIO RISCO
    if (t.contains("ansiedade severa") || t.contains("depressão profunda") ||
            t.contains("insônia") || t.contains("medicamento")) {
      return "MEDIO_RISCO_AMARELO";
    }

    return "BAIXO_RISCO_VERDE";
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
            p.getClassificacaoRisco(),
            p.isAtivo()
    );
  }
}