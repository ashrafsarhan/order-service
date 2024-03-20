package com.codepole.dto.in;

import com.codepole.annotation.HasValidItems;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {

    @NotEmpty
    @Valid
    @HasValidItems
    @Builder.Default
    private List<OrderItemDto> orderItemDtos = new ArrayList<>();

}
