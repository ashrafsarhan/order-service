package com.codepole.dto.out;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @NotNull
    private UUID id;

    @NotNull
    private UUID userId;

    @NotNull
    private Instant date;

    @Builder.Default
    private List<OrderItemDto> orderItems = new ArrayList<>();

}
