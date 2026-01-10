-- Crear tabla de sucursales
CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    franchise_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_branches_franchise 
        FOREIGN KEY (franchise_id) 
        REFERENCES franchises(id) 
        ON DELETE CASCADE,
    CONSTRAINT branches_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT branches_unique_name_per_franchise 
        UNIQUE (franchise_id, name)
);

-- Crear índices
CREATE INDEX idx_branches_franchise_id ON branches(franchise_id);
CREATE INDEX idx_branches_name ON branches(name);

-- Comentarios
COMMENT ON TABLE branches IS 'Stores branch information for each franchise';
COMMENT ON COLUMN branches.franchise_id IS 'Foreign key to franchises table';
