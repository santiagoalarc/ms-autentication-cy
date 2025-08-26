CREATE TABLE IF NOT EXISTS rol (
    id INT PRIMARY KEY,
    rol VARCHAR(255) NOT NULL
);

INSERT INTO rol (id, rol) VALUES (0, 'ADMIN') ON CONFLICT (id) DO NOTHING;
INSERT INTO rol (id, rol) VALUES (1, 'USUARIO') ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS user_entity (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    birth_date BIGINT,
    email VARCHAR(255),
    address VARCHAR(255),
    document_identification VARCHAR(255),
    phone_number VARCHAR(255),
    id_rol INTEGER,
    base_salary VARCHAR(255)
);