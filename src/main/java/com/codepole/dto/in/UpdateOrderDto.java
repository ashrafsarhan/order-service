package com.codepole.dto.in;

import com.codepole.annotation.HasValidItems;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderDto {

    @NotNull
    private UUID orderId;

    @NotEmpty
    @Valid
    @HasValidItems
    @Builder.Default
    private List<OrderItemDto> orderItemDtos = new ArrayList<>();

}
