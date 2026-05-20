package ro.unitbv.restlab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateProductRequest(
        @NotBlank(message = "Name must not be blank")
        String name,

        @Positive(message = "Price must be positive")
        double price,

        @Min(value = 0, message = "Stock cannot be negative")
        int stock,

        @NotBlank(message = "Category must not be blank")
        String category,

        @NotNull(message = "Category ID is required")
        Integer categoryId
) {}
