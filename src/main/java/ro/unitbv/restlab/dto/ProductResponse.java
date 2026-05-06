package ro.unitbv.restlab.dto;

import java.time.LocalDateTime;

public record ProductResponse(
        Integer id,
        String name,
        double price,
        int stock,
        String category,
        LocalDateTime createdAt
) {}
