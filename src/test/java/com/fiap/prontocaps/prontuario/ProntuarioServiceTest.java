package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.common.NotFoundException;
import com.fiap.prontocaps.paciente.Paciente;
import com.fiap.prontocaps.paciente.PacienteRepository;
import com.fiap.prontocaps.prontuario.dto.ProntuarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProntuarioServiceTest {

    @Mock
    private ProntuarioRepository prontuarioRepository;

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private ProntuarioService service;

    private Paciente paciente;

    @BeforeEach
    void setup() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setAtivo(true);
    }

    @Test
    void deveCriarPrimeiroProntuarioComVersao1() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(prontuarioRepository.findByPacienteIdAndAtualTrue(1L))
                .thenReturn(Optional.empty());

        ProntuarioRequest request = new ProntuarioRequest("Primeiro atendimento");

        var response = service.criarPrimeiro(1L, request, "medico1");

        assertEquals(1, response.versao());
        assertTrue(response.versaoAtual());
        assertTrue(response.prontuarioAtivo());

        verify(prontuarioRepository).save(any(Prontuario.class));
    }

    @Test
    void naoDeveCriarPrimeiroSeJaExisteProntuario() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(prontuarioRepository.findByPacienteIdAndAtualTrue(1L))
                .thenReturn(Optional.of(new Prontuario()));

        ProntuarioRequest request = new ProntuarioRequest("Tentativa duplicada");

        assertThrows(BusinessException.class, () ->
                service.criarPrimeiro(1L, request, "medico1")
        );

        verify(prontuarioRepository, never()).save(any());
    }

    @Test
    void deveCriarNovaVersaoIncrementandoVersao() {

        Prontuario prontuarioAtual = new Prontuario();
        prontuarioAtual.setId(1L);
        prontuarioAtual.setVersao(1);
        prontuarioAtual.setAtual(true);
        prontuarioAtual.setAtivo(true);

        when(pacienteRepository.findById(1L))
                .thenReturn(Optional.of(paciente));

        when(prontuarioRepository.findByPacienteIdAndAtualTrue(1L))
                .thenReturn(Optional.of(prontuarioAtual));

        when(prontuarioRepository.save(any(Prontuario.class)))
                .thenAnswer(invocation -> {
                    Prontuario p = invocation.getArgument(0);
                    if (p.getId() == null) {
                        p.setId(2L);
                    }
                    return p;
                });

        var response = service.criarNovaVersao(1L, "Nova evolução", "medico2");

        assertEquals(2, response.versao());
        assertTrue(response.versaoAtual());
        assertTrue(response.prontuarioAtivo());
        assertEquals(2L, response.id());

        verify(prontuarioRepository, times(2)).save(any());
    }


    @Test
    void naoDeveCriarNovaVersaoSemProntuarioAtivo() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(prontuarioRepository.findByPacienteIdAndAtualTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                service.criarNovaVersao(1L, "Nova versão", "medico1")
        );
    }

    @Test
    void deveInativarProntuario() {
        Prontuario prontuario = new Prontuario();
        prontuario.setAtivo(true);
        prontuario.setAtual(true);

        when(prontuarioRepository.findById(10L))
                .thenReturn(Optional.of(prontuario));

        service.inativar(10L);

        assertFalse(prontuario.isAtivo());
        assertFalse(prontuario.getAtual());

        verify(prontuarioRepository).save(prontuario);
    }

    @Test
    void naoDeveCriarProntuarioParaPacienteInativo() {
        paciente.setAtivo(false);
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        ProntuarioRequest request = new ProntuarioRequest("Teste");

        assertThrows(BusinessException.class, () ->
                service.criarPrimeiro(1L, request, "medico1")
        );
    }

    @Test
    void deveLancarErroQuandoPacienteNaoExiste() {
        when(pacienteRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                service.criarPrimeiro(99L, new ProntuarioRequest("Teste"), "medico1")
        );
    }
}