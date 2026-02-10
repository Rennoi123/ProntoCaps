package com.fiap.prontocaps.paciente;

import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.common.NotFoundException;
import com.fiap.prontocaps.paciente.dto.PacienteRequest;
import com.fiap.prontocaps.paciente.dto.PacienteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public PacienteResponse criar(PacienteRequest pacienteRequest) {
        validarCpfUnico(pacienteRequest.cpf());

        Paciente paciente = new Paciente();
        paciente.setNome(pacienteRequest.nome());
        paciente.setCpf(pacienteRequest.cpf());
        paciente.setDataNascimento(pacienteRequest.dataNascimento());
        pacienteRepository.save(paciente);

        return toResponse(paciente);
    }

    public PacienteResponse buscarPorId(Long id) {
        return toResponse(buscarPacienteAtivo(id));
    }

    public List<PacienteResponse> list() {
        return pacienteRepository.findAll().stream().map(this::toResponse).toList();
    }

    public PacienteResponse update(Long id, PacienteRequest pacienteRequest) {
        Paciente paciente = buscarPacienteAtivo(id);

        if (!paciente.getCpf().equals(pacienteRequest.cpf()) && pacienteRepository.findByCpf(pacienteRequest.cpf()).isPresent()) {
            throw new BusinessException("CPF ja cadastrado");
        }

        paciente.setNome(pacienteRequest.nome());
        paciente.setCpf(pacienteRequest.cpf());
        paciente.setDataNascimento(pacienteRequest.dataNascimento());
        pacienteRepository.save(paciente);
        return toResponse(paciente);
    }

    public void inativar(Long id) {
        Paciente paciente = buscarPacienteAtivo(id);
        paciente.setAtivo(false);
        pacienteRepository.save(paciente);
    }

    private PacienteResponse toResponse(Paciente paciente) {
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNome(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.isAtivo()
        );
    }

    private void validarCpfUnico(String cpf) {
        if (pacienteRepository.existsByCpf(cpf)) {
            throw new BusinessException("Ja existe paciente cadastrado com este CPF");
        }
    }

    private Paciente buscarPacienteAtivo(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Paciente nao encontrado"));

        if (!paciente.isAtivo()) {
            throw new BusinessException("Paciente esta inativo");
        }

        return paciente;
    }
}
