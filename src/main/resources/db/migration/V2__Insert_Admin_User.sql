INSERT INTO app_user (email, password, name, roles, active)
VALUES
    ('admin@prontocaps.com', '$2a$10$tqs3qCxLd9k3.8jOV4qpBeD4ueV7wLevEEg58zdclLOSKHsCAfT76', 'Administrador Sistema', 'ROLE_ADMIN', true)
    ON CONFLICT (email) DO NOTHING;