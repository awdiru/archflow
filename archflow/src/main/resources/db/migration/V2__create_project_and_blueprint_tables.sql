-- 1. Таблица проектов
CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    status VARCHAR(50) NOT NULL,
    total_budget DECIMAL(19, 2),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_projects_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. Таблица чертежей
CREATE TABLE IF NOT EXISTS blueprints (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    file_url TEXT NOT NULL,
    version INTEGER DEFAULT 1,
    is_approved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_blueprints_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- 3. Таблица справочника товаров (Catalog)
CREATE TABLE IF NOT EXISTS catalog_items (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    supplier_url TEXT,
    image_url TEXT,
    price DECIMAL(19, 2) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    sku VARCHAR(100),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_catalog_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

-- 4. Таблица интерактивных меток (Markers)
-- Используем JSONB для координат, чтобы эффективно хранить полигоны/точки
CREATE TABLE IF NOT EXISTS blueprint_markers (
    id BIGSERIAL PRIMARY KEY,
    blueprint_id BIGINT NOT NULL,
    catalog_item_id BIGINT NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL,
    coordinates JSONB NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_markers_blueprint FOREIGN KEY (blueprint_id) REFERENCES blueprints(id) ON DELETE CASCADE,
    CONSTRAINT fk_markers_item FOREIGN KEY (catalog_item_id) REFERENCES catalog_items(id) ON DELETE CASCADE
);

-- Индексы для ускорения поиска
CREATE INDEX idx_projects_owner ON projects(owner_id);
CREATE INDEX idx_blueprints_project ON blueprints(project_id);
-- Индекс GIN для быстрого поиска внутри JSON-координат (на будущее)
CREATE INDEX idx_markers_coordinates ON blueprint_markers USING GIN (coordinates);