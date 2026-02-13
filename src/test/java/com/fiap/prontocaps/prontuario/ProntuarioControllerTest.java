package com.fiap.prontocaps.prontuario;

import com.fiap.prontocaps.prontuario.dto.ProntuarioResponse;
import com.fiap.prontocaps.security.JwtTokenProvider;
import com.fiap.prontocaps.security.JwtAuthenticationEntryPoint;
import com.fiap.prontocaps.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProntuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProntuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProntuarioService prontuarioService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void deveListarProntuariosPorPaciente() throws Exception {
        Long pacienteId = 1L;

        when(prontuarioService.listByPaciente(pacienteId))
                .thenReturn(List.of(
                        new ProntuarioResponse(1L, pacienteId, null, "medico", "desc", "alta", 1, true, true, "string teste")
                ));

        mockMvc.perform(get("/pacientes/{pacienteId}/prontuarios", pacienteId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].versao").value(1));
    }
}