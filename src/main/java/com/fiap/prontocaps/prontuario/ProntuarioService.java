package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.alerta.Alerta;
import com.fiap.prontocaps.alerta.AlertaRepository;
import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.common.NotFoundException;
import com.fiap.prontocaps.paciente.Paciente;
import com.fiap.prontocaps.paciente.PacienteRepository;
import com.fiap.prontocaps.prontuario.dto.ProntuarioRequest;
import com.fiap.prontocaps.prontuario.dto.ProntuarioResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final AlertaRepository alertaRepository;

    public ProntuarioService(ProntuarioRepository prontuarioRepository, PacienteRepository pacienteRepository, AlertaRepository alertaRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.alertaRepository = alertaRepository;
    }

    public ProntuarioResponse criarPrimeiro(Long pacienteId, ProntuarioRequest prontuarioRequest, String profissionalUsername) {
        Paciente paciente = buscarPacientePorId(pacienteId);

        if (prontuarioRepository.findByPacienteIdAndAtualTrue(pacienteId).isPresent()) {
            throw new BusinessException("Paciente ja possui prontuario. Utilize criacao de nova versao.");
        }

        Prontuario prontuario = new Prontuario();
        prontuario.setPaciente(paciente);
        prontuario.setDescricao(prontuarioRequest.descricao());
        prontuario.setProfissionalUsername(profissionalUsername);
        prontuario.setDataRegistro(LocalDateTime.now());
        prontuario.setVersao(1);
        prontuario.setAtivo(true);
        prontuario.setAtual(true);
        prontuario.setClassificacaoRisco(calcularRisco(prontuarioRequest.descricao()));

        String dadosParaHash = prontuario.getDescricao()
                + prontuario.getDataRegistro().toString()
                + prontuario.getProfissionalUsername()
                + "CAPS";

        prontuario.setAssinaturaDigital(gerarHashSHA256(dadosParaHash));

        prontuarioRepository.save(prontuario);

        baixaAlertaAutomatico(paciente);

        return toResponse(prontuario);
    }

    private void baixaAlertaAutomatico(Paciente paciente) {
        List<Alerta> alertasPendentes = alertaRepository.findByPacienteIdAndStatus(paciente.getId(), "PENDENTE");

        if (!alertasPendentes.isEmpty()) {
            alertasPendentes.forEach(alerta -> {
                alerta.setStatus("RESOLVIDO");
                alerta.setMotivo(alerta.getMotivo() + " (Resolvido automaticamente via retorno médico)");
            });
            alertaRepository.saveAll(alertasPendentes);
        }
    }

    public ProntuarioResponse criarNovaVersao(Long pacienteId, String novaDescricao, String profissionalUsername) {
        Paciente paciente = buscarPacientePorId(pacienteId);

        Prontuario prontuarioAnterior = prontuarioRepository
                .findByPacienteIdAndAtualTrue(pacienteId)
                .orElseThrow(() -> new BusinessException("Paciente nao possui prontuario ativo"));

        prontuarioAnterior.setAtual(false);
        prontuarioRepository.save(prontuarioAnterior);

        Prontuario novaVersao = new Prontuario();
        novaVersao.setPaciente(paciente);
        novaVersao.setDescricao(novaDescricao);
        novaVersao.setProfissionalUsername(profissionalUsername);
        novaVersao.setVersao(prontuarioAnterior.getVersao() + 1);
        novaVersao.setAtivo(true);
        novaVersao.setAtual(true);
        novaVersao.setDataRegistro(LocalDateTime.now());
        novaVersao.setClassificacaoRisco(calcularRisco(novaDescricao));

        String dadosParaHash = novaDescricao
                + novaVersao.getDataRegistro().toString()
                + profissionalUsername
                + prontuarioAnterior.getAssinaturaDigital();

        novaVersao.setAssinaturaDigital(gerarHashSHA256(dadosParaHash));

        baixaAlertaAutomatico(paciente);

        return toResponse(prontuarioRepository.save(novaVersao));
    }

    private String gerarHashSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar assinatura digital", e);
        }
    }

    private String calcularRisco(String texto) {
        if (texto == null) return "BAIXO_RISCO_VERDE";
        String t = texto.toLowerCase();

        if (t.contains("suicidio") || t.contains("suicídio") ||
                t.contains("agressivo") || t.contains("surto") ||
                t.contains("alucinação") || t.contains("alucinações") ||
                t.contains("risco de vida") || t.contains("armado")) {
            return "ALTO_RISCO_VERMELHO";
        }

        if (t.contains("ansiedade") || t.contains("depressão") ||
                t.contains("insônia") || t.contains("medicamento") ||
                t.contains("instável") || t.contains("choroso")) {
            return "MEDIO_RISCO_AMARELO";
        }

        return "BAIXO_RISCO_VERDE";
    }

    public List<ProntuarioResponse> listByPaciente(Long pacienteId) {
        return prontuarioRepository.findByPacienteIdOrderByVersaoDesc(pacienteId)
                .stream().map(this::toResponse).toList();
    }

    public void inativar(Long prontuarioId) {
        Prontuario prontuario = buscarProntuarioPorId(prontuarioId);
        prontuario.setAtivo(false);
        prontuario.setAtual(false);
        prontuarioRepository.save(prontuario);
    }

    private ProntuarioResponse toResponse(Prontuario prontuario) {
        return new ProntuarioResponse(
                prontuario.getId(),
                prontuario.getPaciente().getId(),
                prontuario.getDataRegistro(),
                prontuario.getProfissionalUsername(),
                prontuario.getDescricao(),
                prontuario.getClassificacaoRisco(),
                prontuario.getVersao(),
                prontuario.getAtual(),
                prontuario.isAtivo(),
                prontuario.getAssinaturaDigital()
        );
    }

    private Prontuario buscarProntuarioPorId(Long prontuarioId) {
        return prontuarioRepository.findById(prontuarioId)
                .orElseThrow(() -> new NotFoundException("Prontuario nao encontrado"));
    }

    private Paciente buscarPacientePorId(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new NotFoundException("Paciente nao encontrado"));

        if (!paciente.isAtivo()) {
            throw new BusinessException("Nao e permitido criar prontuario para paciente inativo");
        }

        return paciente;
    }
}