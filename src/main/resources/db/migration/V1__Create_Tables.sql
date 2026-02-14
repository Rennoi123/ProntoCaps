CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          name VARCHAR(255),
                          roles VARCHAR(50),
                          active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE paciente (
                          id BIGSERIAL PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          cpf VARCHAR(14) NOT NULL UNIQUE,
                          data_nascimento DATE NOT NULL,
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE prontuario (
                            id BIGSERIAL PRIMARY KEY,
                            paciente_id BIGINT NOT NULL,
                            data_registro TIMESTAMP NOT NULL,
                            profissional_username VARCHAR(255) NOT NULL,
                            descricao VARCHAR(2000) NOT NULL,
                            classificacao_risco VARCHAR(50),
                            versao INTEGER NOT NULL,
                            atual BOOLEAN NOT NULL,
                            ativo BOOLEAN NOT NULL DEFAULT TRUE,
                            assinatura_digital VARCHAR(512),
                            created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                            CONSTRAINT fk_prontuario_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id)
);