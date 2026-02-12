package com.fiap.prontocaps.paciente;

import com.fiap.prontocaps.common.BusinessException;
import com.fiap.prontocaps.common.NotFoundException;
import com.fiap.prontocaps.paciente.dto.PacienteRequest;
import com.fiap.prontocaps.prontuario.Prontuario;
import com.fiap.prontocaps.prontuario.ProntuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private ProntuarioRepository prontuarioRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente paciente;

    @BeforeEach
    void setup() {
        paciente = new Paciente();
        paciente.setId(1L);
        paciente.setNome("João da Silva");
        paciente.setCpf("12345678900");
        paciente.setDataNascimento(LocalDate.of(1990, 1, 1));
        paciente.setAtivo(true);
        paciente.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void deveCriarPacienteComSucesso() {
        PacienteRequest request = new PacienteRequest(
                "João da Silva",
                "12345678900",
                LocalDate.of(1990, 1, 1)
        );

        when(pacienteRepository.existsByCpf(request.cpf())).thenReturn(false);
        when(pacienteRepository.save(any(Paciente.class)))
                .thenAnswer(invocation -> {
                    Paciente p = invocation.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        var response = pacienteService.criar(request);

        assertNotNull(response.id());
        assertEquals("João da Silva", response.nome());
        assertTrue(response.ativo());

        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    void naoDeveCriarPacienteComCpfDuplicado() {
        PacienteRequest request = new PacienteRequest(
                "Maria",
                "12345678900",
                LocalDate.of(1985, 5, 10)
        );

        when(pacienteRepository.existsByCpf(request.cpf())).thenReturn(true);

        assertThrows(BusinessException.class, () ->
                pacienteService.criar(request)
        );

        verify(pacienteRepository, never()).save(any());
    }

    @Test
    void deveListarPacientes() {

        Paciente p1 = new Paciente();
        p1.setId(1L);
        p1.setNome("Paciente 1");
        p1.setCpf("11111111111");
        p1.setAtivo(true);

        Paciente p2 = new Paciente();
        p2.setId(2L);
        p2.setNome("Paciente 2");
        p2.setCpf("22222222222");
        p2.setAtivo(true);

        when(pacienteRepository.findAll()).thenReturn(List.of(p1, p2));

        var result = pacienteService.list();

        assertEquals(2, result.size());
        assertEquals("Paciente 1", result.get(0).nome());
        assertEquals("Paciente 2", result.get(1).nome());

        verify(pacienteRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistirPaciente() {

        when(pacienteRepository.findAll()).thenReturn(List.of());

        var result = pacienteService.list();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deveAtualizarPacienteComSucesso() {

        PacienteRequest request = new PacienteRequest(
                "Novo Nome",
                "99999999999",
                LocalDate.of(1995, 5, 5)
        );

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.findByCpf(request.cpf()))
                .thenReturn(Optional.empty());

        when(pacienteRepository.save(any(Paciente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = pacienteService.update(1L, request);

        assertEquals("Novo Nome", response.nome());
        assertEquals("99999999999", response.cpf());
    }

    @Test
    void naoDeveAtualizarPacienteComCpfDuplicado() {

        PacienteRequest request = new PacienteRequest(
                "Novo Nome",
                "99999999999",
                LocalDate.of(1995, 5, 5)
        );

        Paciente outroPaciente = new Paciente();
        outroPaciente.setId(2L);
        outroPaciente.setCpf("99999999999");

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(pacienteRepository.findByCpf(request.cpf()))
                .thenReturn(Optional.of(outroPaciente));

        assertThrows(BusinessException.class, () ->
                pacienteService.update(1L, request)
        );

        verify(pacienteRepository, never()).save(any());
    }

    @Test
    void deveAtualizarPacienteMantendoMesmoCpf() {

        PacienteRequest request = new PacienteRequest(
                "Nome Atualizado",
                paciente.getCpf(), // mesmo CPF
                LocalDate.of(1990, 1, 1)
        );

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        when(pacienteRepository.save(any(Paciente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = pacienteService.update(1L, request);

        assertEquals("Nome Atualizado", response.nome());
        assertEquals(paciente.getCpf(), response.cpf());
    }

    @Test
    void deveBuscarPacienteAtivoPorId() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        var response = pacienteService.buscarPorId(1L);

        assertEquals(paciente.getId(), response.id());
        assertTrue(response.ativo());
    }

    @Test
    void naoDeveBuscarPacienteInativo() {
        paciente.setAtivo(false);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));

        assertThrows(BusinessException.class, () ->
                pacienteService.buscarPorId(1L)
        );
    }

    @Test
    void deveLancarErroQuandoPacienteNaoExiste() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                pacienteService.buscarPorId(99L)
        );
    }

    /*@Test
    void naoDeveInativarPacienteComProntuarioAtivo() {

        paciente.setAtivo(true);

        when(pacienteRepository.findById(1L))
                .thenReturn(Optional.of(paciente));

        when(prontuarioRepository.findByPacienteIdAndAtualTrue(1L))
                .thenReturn(Optional.of(new Prontuario()));

        assertThrows(BusinessException.class, () ->
                pacienteService.inativar(1L)
        );

        verify(pacienteRepository, never()).save(any());
    }

    @Test
    void deveInativarPacienteSemProntuarioAtivo() {

        paciente.setAtivo(true);

        when(pacienteRepository.findById(1L))
                .thenReturn(Optional.of(paciente));

        when(prontuarioRepository.findByPacienteIdAndAtualTrue(1L))
                .thenReturn(Optional.empty());

        pacienteService.inativar(1L);

        assertFalse(paciente.isAtivo());
        verify(pacienteRepository).save(paciente);
    }*/
}