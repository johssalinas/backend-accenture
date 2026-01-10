-- Crear tabla de productos
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    branch_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_branch 
        FOREIGN KEY (branch_id) 
        REFERENCES branches(id) 
        ON DELETE CASCADE,
    CONSTRAINT products_name_not_empty CHECK (LENGTH(TRIM(name)) > 0),
    CONSTRAINT products_stock_non_negative CHECK (stock >= 0),
    CONSTRAINT products_unique_name_per_branch 
        UNIQUE (branch_id, name)
);

-- Crear índices
CREATE INDEX idx_products_branch_id ON products(branch_id);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_stock ON products(stock DESC);

-- Comentarios
COMMENT ON TABLE products IS 'Stores product information for each branch';
COMMENT ON COLUMN products.stock IS 'Current stock level, must be non-negative';
COMMENT ON COLUMN products.branch_id IS 'Foreign key to branches table';
