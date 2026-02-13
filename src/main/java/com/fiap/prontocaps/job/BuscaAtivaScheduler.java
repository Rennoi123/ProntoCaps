package com.fiap.prontocaps.job;

import com.fiap.prontocaps.alerta.Alerta;
import com.fiap.prontocaps.alerta.AlertaRepository;
import com.fiap.prontocaps.paciente.Paciente;
import com.fiap.prontocaps.paciente.PacienteRepository;
import com.fiap.prontocaps.prontuario.Prontuario;
import com.fiap.prontocaps.prontuario.ProntuarioRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@EnableScheduling
public class BuscaAtivaScheduler {

    private final ProntuarioRepository prontuarioRepository;
    private final AlertaRepository alertaRepository;
    private final PacienteRepository pacienteRepository;

    public BuscaAtivaScheduler(ProntuarioRepository prontuarioRepository, AlertaRepository alertaRepository, PacienteRepository pacienteRepository) {
        this.prontuarioRepository = prontuarioRepository;
        this.alertaRepository = alertaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void verificarAbandonoTratamento() {
        System.out.println(">>> Iniciando Job de Busca Ativa...");

        pacienteRepository.findAll().forEach(pacienteLoop -> {
            if (!pacienteLoop.isAtivo()) return;

            prontuarioRepository.findByPacienteIdAndAtualTrue(pacienteLoop.getId())
                    .ifPresent(ultimoProntuario -> {
                        Paciente pacienteAlvo = ultimoProntuario.getPaciente();

                        if (isAltoRisco(ultimoProntuario) && isAtrasado(ultimoProntuario, 7)) {
                            gerarAlerta(pacienteAlvo, "URGENTE: Paciente de Alto Risco sem retorno recente.");
                        }
                        else if (isAtrasado(ultimoProntuario, 30)) {
                            gerarAlerta(pacienteAlvo, "Suspeita de abandono de tratamento (30 dias sem visita).");
                        }
                    });
        });
    }

    private boolean isAltoRisco(Prontuario p) {
        if (p.getDescricao() == null) return false;
        String desc = p.getDescricao().toLowerCase();
        return desc.contains("suicidio") || desc.contains("suicídio") ||
                desc.contains("agressivo") || desc.contains("surto") ||
                desc.contains("alucinação") || desc.contains("alucinações") ||
                desc.contains("risco de vida") || desc.contains("armado");
    }

    private boolean isAtrasado(Prontuario p, int dias) {
        return p.getDataRegistro().isBefore(LocalDateTime.now().minusDays(dias));
    }

    private void gerarAlerta(Paciente p, String motivo) {
        if (!alertaRepository.existsByPacienteIdAndStatus(p.getId(), "PENDENTE")) {
            Alerta alerta = new Alerta();
            alerta.setPaciente(p);
            alerta.setMotivo(motivo);
            alerta.setStatus("PENDENTE");
            alertaRepository.save(alerta);
            System.out.println("!!! ALERTA GERADO PARA: " + p.getNome());
        }
    }
}