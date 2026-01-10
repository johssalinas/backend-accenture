-- Crear extensión para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Crear tabla de franquicias
CREATE TABLE franchises (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT franchises_name_not_empty CHECK (LENGTH(TRIM(name)) > 0)
);

-- Crear índice para búsquedas por nombre
CREATE INDEX idx_franchises_name ON franchises(name);

-- Comentarios
COMMENT ON TABLE franchises IS 'Stores franchise information';
COMMENT ON COLUMN franchises.name IS 'Unique franchise name';
