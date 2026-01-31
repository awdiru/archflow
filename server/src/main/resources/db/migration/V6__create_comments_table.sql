-- 5. Таблица комментариев (Comments)
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    blueprint_id BIGINT NOT NULL,
    marker_id BIGINT,
    parent_id BIGINT,
    is_resolved BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Внешние ключи
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_blueprint FOREIGN KEY (blueprint_id) REFERENCES blueprints(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_marker FOREIGN KEY (marker_id) REFERENCES blueprint_markers(id) ON DELETE SET NULL,
    CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
);

-- Индексы для быстрого поиска обсуждений
CREATE INDEX idx_comments_blueprint ON comments(blueprint_id);
CREATE INDEX idx_comments_parent ON comments(parent_id);
CREATE INDEX idx_comments_marker ON comments(marker_id);