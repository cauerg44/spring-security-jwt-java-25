CREATE TABLE IF NOT EXISTS tb_user (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'USER'))
    );

CREATE INDEX idx_user_login ON tb_user(login);