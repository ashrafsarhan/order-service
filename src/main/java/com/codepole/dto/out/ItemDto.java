package com.codepole.dto.out;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @NotNull
    private UUID id;

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin(value = "0.00", message = "Item price must be higher than 0.00")
    private BigDecimal unitPrice;

}
