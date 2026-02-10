package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.common.NotFoundException;
import com.fiap.prontocaps.paciente.Paciente;
import com.fiap.prontocaps.paciente.PacienteRepository;
import com.fiap.prontocaps.prontuario.dto.ProntuarioRequest;
import com.fiap.prontocaps.prontuario.dto.ProntuarioResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProntuarioService {

    private final ProntuarioRepository prontuarioRepository;
    private final PacienteRepository pacienteRepository;

    public ProntuarioService(ProntuarioRepository prontuarioRepository, PacienteRepository pacienteRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.pacienteRepository = pacienteRepository;
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
        prontuarioRepository.save(prontuario);

        return toResponse(prontuario);
    }

    public ProntuarioResponse criarNovaVersao(Long pacienteId, String novaDescricao, String profissionalUsername) {
        Paciente paciente = buscarPacientePorId(pacienteId);

        Prontuario prontuarioAtualAtivo = prontuarioRepository
                .findByPacienteIdAndAtualTrue(pacienteId)
                .orElseThrow(() -> new BusinessException("Paciente nao possui prontuario ativo"));

        prontuarioAtualAtivo.setAtual(false);
        prontuarioRepository.save(prontuarioAtualAtivo);

        Prontuario novaVersao = new Prontuario();
        novaVersao.setPaciente(paciente);
        novaVersao.setDescricao(novaDescricao);
        novaVersao.setProfissionalUsername(profissionalUsername);
        novaVersao.setVersao(prontuarioAtualAtivo.getVersao() + 1);
        novaVersao.setAtivo(true);
        novaVersao.setAtual(true);
        novaVersao.setDataRegistro(LocalDateTime.now());

        return toResponse(prontuarioRepository.save(novaVersao));
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
                prontuario.getVersao(),
                prontuario.getAtual(),
                prontuario.isAtivo()
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
