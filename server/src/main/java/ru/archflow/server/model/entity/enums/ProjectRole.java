package ru.archflow.server.model.entity.enums;

public enum ProjectRole {
    OWNER,       // Ведущий дизайнер / Создатель
    EDITOR,      // Дизайнеры с полным доступом
    CONTRIBUTOR, // Дизайнеры с пре-модерацией
    VIEWER       // Заказчики / Наблюдатели
}