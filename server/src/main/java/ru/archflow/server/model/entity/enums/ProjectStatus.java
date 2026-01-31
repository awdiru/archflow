package ru.archflow.server.model.entity.enums;

public enum ProjectStatus {
    DRAFT,          // Черновик
    IN_PROGRESS,    // В работе
    WAITING_REVIEW, // Ожидает проверки заказчиком
    COMPLETED,      // Завершен
    ARCHIVED        // В архиве
}