INSERT INTO app_user (email, password, name, roles, active)
VALUES
    ('admin@prontocaps.com', '$2a$12$bvsPmapbE4veYHoORCcqWunN.DlM7TyifUApTsDqg3dpfukWHeSne', 'Administrador Sistema', 'ROLE_ADMIN', true)
    ON CONFLICT (email) DO NOTHING;