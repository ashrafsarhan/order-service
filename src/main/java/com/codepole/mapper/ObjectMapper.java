package com.codepole.mapper;

import com.codepole.domain.Item;
import com.codepole.domain.Order;
import com.codepole.domain.OrderItem;
import com.codepole.dto.in.CreateItemDto;
import com.codepole.dto.out.ItemDto;
import com.codepole.dto.out.OrderDto;
import com.codepole.dto.out.OrderItemDto;
import java.util.stream.Collectors;

public class ObjectMapper {
    public static Item toItem(CreateItemDto createItemDto) {
        return Item.builder()
                .name(createItemDto.getName())
                .unitPrice(createItemDto.getUnitPrice())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .unitPrice(item.getUnitPrice())
                .build();
    }

    public static OrderDto toOrderDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderItems(order.getOrderItems().stream()
                        .map(ObjectMapper::toOrderItemDto).collect(Collectors.toList()))
                .build();
    }

    public static OrderItemDto toOrderItemDto(OrderItem orderItem) {
        return OrderItemDto.builder()
                .itemId(orderItem.getItem().getId())
                .amount(orderItem.getAmount())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }

}
