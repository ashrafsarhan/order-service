package com.codepole.dto.in;

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
public class UpdateItemDto {

    @NotNull(message = "Item id must not be null")
    private UUID id;

    @NotBlank(message = "Item name must not be blank")
    private String name;

    @NotNull
    @DecimalMin(value = "1.00", message = "Item price must be higher than 1.00")
    private BigDecimal unitPrice;

}
