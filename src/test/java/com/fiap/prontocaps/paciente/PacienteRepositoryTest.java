package com.fiap.prontocaps.paciente;

import com.fiap.prontocaps.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate; // Importação necessária
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class PacienteRepositoryTest {

    @Autowired
    private PacienteRepository repository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void deveSalvarEBuscarPacientePorCpf() {

        Paciente paciente = new Paciente();
        paciente.setNome("Paciente Repo");
        paciente.setCpf("99999999999");
        paciente.setAtivo(true);
        paciente.setDataNascimento(LocalDate.of(1990, 1, 1));

        repository.save(paciente);

        var encontrado = repository.findByCpf("99999999999");

        assertTrue(encontrado.isPresent());
    }
}