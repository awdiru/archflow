-- 1. Создание таблицы компаний
CREATE TABLE IF NOT EXISTS companies (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL UNIQUE,
    inn VARCHAR(20) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Обновление таблицы пользователей
ALTER TABLE users ADD COLUMN company_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_company
FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL;

-- 3. Обновление таблицы проектов (Multi-tenancy)
-- Проекты теперь жестко привязаны к компании владельца
ALTER TABLE projects ADD COLUMN company_id BIGINT;
ALTER TABLE projects ADD CONSTRAINT fk_projects_company
FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;

-- Индексы для фильтрации
CREATE INDEX idx_users_company ON users(company_id);
CREATE INDEX idx_projects_company ON projects(company_id);