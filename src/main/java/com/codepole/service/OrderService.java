package com.codepole.service;

import com.codepole.domain.Order;
import com.codepole.domain.OrderItem;
import com.codepole.domain.OrderItemId;
import com.codepole.dto.in.CreateOrderDto;
import com.codepole.dto.in.OrderItemDto;
import com.codepole.dto.in.UpdateOrderDto;
import com.codepole.dto.out.OrderDto;
import com.codepole.exception.OrderNotFoundException;
import com.codepole.mapper.ObjectMapper;
import com.codepole.repository.ItemRepository;
import com.codepole.repository.OrderItemRepository;
import com.codepole.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ItemRepository itemRepository;

    private final OrderItemRepository orderItemRepository;

    public UUID createOrder(UUID userId, CreateOrderDto createOrderDto) {
        var order = orderRepository.save(Order.builder().userId(userId).build());
        setOrderItems(order, createOrderDto.getOrderItemDtos());
        return order.getId();
    }

    public OrderDto getOrder(UUID orderId) throws OrderNotFoundException {
        return orderRepository.findById(orderId)
                .map(ObjectMapper::toOrderDto)
                .orElseThrow(() -> new OrderNotFoundException(orderId.toString()));
    }

    public Page<OrderDto> getOrdersByUserId(UUID userId, Pageable pageable) {
        return orderRepository.findAllOrderByUserId(userId, pageable)
                .map(ObjectMapper::toOrderDto);
    }

    public UUID updateOrder(UpdateOrderDto updateOrderDto) throws OrderNotFoundException {
        return orderRepository.findById(updateOrderDto.getOrderId())
                .map(order -> {
                    setOrderItems(order, updateOrderDto.getOrderItemDtos());
                    return order.getId();
                })
                .orElseThrow(() -> new OrderNotFoundException(updateOrderDto.getOrderId().toString()));
    }

    public void deleteOrder(UUID orderId) throws OrderNotFoundException {
        orderRepository.findById(orderId).ifPresentOrElse(order ->
                        orderRepository.deleteById(orderId),
                () -> {
                    throw new OrderNotFoundException(orderId.toString());
                });
    }

    public BigDecimal getTotalPriceByUserIdAndDateRange(UUID userId, Instant dateAfter, Instant dateBefore) {
        return orderItemRepository.findAllByOrderUserIdAndOrderDateBetween(userId, dateAfter, dateBefore)
                .stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void setOrderItems(Order order, List<OrderItemDto> orderItemDtos) {
        orderItemDtos.forEach(orderItemDto -> itemRepository.findById(orderItemDto.getItemId())
                .ifPresent(item -> {
                    var orderItem = OrderItem.builder()
                            .id(OrderItemId.builder()
                                    .orderId(order.getId())
                                    .itemId(item.getId())
                                    .build())
                            .order(order)
                            .item(item)
                            .amount(orderItemDto.getAmount())
                            .totalPrice(calculateTotalPriceWithDiscount(orderItemDto.getAmount(), item.getUnitPrice()))
                            .build();
                    order.addOrderItem(orderItem);
                }));
        orderRepository.save(order);
    }

    private BigDecimal calculateTotalPriceWithDiscount(int itemCount,  BigDecimal unitPrice) {
        var totalPrice = BigDecimal.ZERO;
        if (itemCount >= 5) {
            unitPrice = unitPrice.multiply(BigDecimal.valueOf(0.90)); // 10% discount
        }
        if (itemCount > 10) {
            unitPrice = unitPrice.multiply(BigDecimal.valueOf(0.85)); // 15% discount
        }
        totalPrice = totalPrice.add(unitPrice.multiply(BigDecimal.valueOf(itemCount)));
        return totalPrice;
    }


}
