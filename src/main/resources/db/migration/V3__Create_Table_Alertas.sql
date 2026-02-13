CREATE TABLE alerta_busca_ativa (
                                    id BIGSERIAL PRIMARY KEY,
                                    paciente_id BIGINT NOT NULL,
                                    motivo VARCHAR(255) NOT NULL,
                                    status VARCHAR(50) NOT NULL,
                                    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
                                    CONSTRAINT fk_alerta_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);