-- Crear índices compuestos para consultas comunes
CREATE INDEX idx_branches_franchise_created 
    ON branches(franchise_id, created_at DESC);

CREATE INDEX idx_products_branch_stock 
    ON products(branch_id, stock DESC);

-- Comentarios
COMMENT ON INDEX idx_branches_franchise_created IS 'Composite index for franchise branches ordered by creation date';
COMMENT ON INDEX idx_products_branch_stock IS 'Composite index for finding top stock products by branch';
