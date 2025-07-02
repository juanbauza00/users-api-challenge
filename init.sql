-- Script de inicio para tener los datos frescos (eliminar para uso definitivo)
DROP TABLE IF EXISTS clients_groups CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS owners CASCADE;
------------------------------------


-- OWNERS
CREATE TABLE IF NOT EXISTS owners (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
    );

-- CLIENTS
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id BIGINT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES owners(id)
    );

-- GROUPS
CREATE TABLE IF NOT EXISTS groups (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    owner_id BIGINT NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES owners(id)
    );

-- JOIN TABLE
CREATE TABLE IF NOT EXISTS clients_groups (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT,
    group_id BIGINT,
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (group_id) REFERENCES groups(id),
    UNIQUE(client_id, group_id)
    );


-------- INDEXES (optimiza la busqueda) -------------------
CREATE INDEX IF NOT EXISTS idx_clients_owner_id ON clients(owner_id);
CREATE INDEX IF NOT EXISTS idx_groups_owner_id ON groups(owner_id);
CREATE INDEX IF NOT EXISTS idx_clients_groups_client_id ON clients_groups(client_id);
CREATE INDEX IF NOT EXISTS idx_clients_groups_group_id ON clients_groups(group_id);


------------------------ INSERTS --------------------------
INSERT INTO owners (nombre) VALUES
    ('Juan Bauza'),
    ('Salva Diaz'),
    ('Lio Messi');

INSERT INTO clients (nombre, apellido, fecha_nacimiento, owner_id) VALUES
    ('Ana', 'González', '1990-05-15', 1),
    ('Pedro', 'Martínez', '1985-08-20', 1),
    ('Laura', 'Rodríguez', '1992-12-10', 2),
    ('Juan', 'Lopez', '1990-05-15', 1),
    ('Fernando', 'Sanchez', '1985-08-20', 1),
    ('Martín', 'Villa', '1992-10-10', 2),
    ('El bananero', 'Consigli', '1990-05-15', 1),
    ('Lio', 'Solis', '2000-07-20', 2),
    ('Ana', 'Rodríguez', '2000-12-10', 2),
    ('Ana', 'González', '1990-05-15', 3),
    ('Pedro', 'Martínez', '1985-08-20', 3),
    ('Laura', 'Rodríguez', '1992-12-10', 3);

INSERT INTO groups (nombre, descripcion, owner_id) VALUES
    -- Grupos del owner 1
    ('Evento 16/08', 'Clientes que compraron entrada al evento del 16/08', 1),
    ('Evento 23/08', 'Clientes que compraron entrada al evento del 16/08', 1),
    ('Grupo VIP', 'Clientes VIP', 1),
    ('Grupo Platinum', 'Clientes platino', 1),
    -- Grupos del owner 2
    ('Grupo Mayorista', 'Clientes vta por mayor', 2),
    ('Grupo Minorista', 'Clientes vta por menor', 2);

INSERT INTO clients_groups (client_id, group_id) VALUES
    (1,1), (1,3), (2,1), (2,4), (4,2),
    (3,1), (6,2), (8,2);