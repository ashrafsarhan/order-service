package com.codepole.controller;

import com.codepole.api.ApiResponse;
import com.codepole.dto.in.CreateOrderDto;
import com.codepole.dto.in.UpdateOrderDto;
import com.codepole.dto.out.OrderDto;
import com.codepole.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/orders")
@Tag(name = "Order", description = "Order API manages the orders.")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create order", description = "Create a new order entity with with list of items and quantity.")
    @PostMapping(name = "CreateOrder", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ApiResponse<UUID> createOrder(
            @RequestHeader(value = "USER-ID") @NotNull UUID userId,
            @RequestBody @Valid CreateOrderDto createOrderDto) {
        var orderId = orderService.createOrder(userId, createOrderDto);
        return ApiResponse.ok(orderId);
    }

    @Operation(summary = "Update order", description = "Update an existing order entity with with list of items and quantity.")
    @PutMapping(name = "UpdateOrder", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ApiResponse<UUID> updateOrder(
            @RequestBody @Valid UpdateOrderDto updateOrderDto) {
        var orderId = orderService.updateOrder(updateOrderDto);
        return ApiResponse.ok(orderId);
    }

    @Operation(summary = "Get total price for a specific user orders by date range", description = "Get total price for a specific user orders by date range.")
    @GetMapping(name = "GetUserOrdersTotalPrice", value = "/total-price", produces = APPLICATION_JSON_VALUE)
    ApiResponse<BigDecimal> getUserOrdersByDateRange(
            @RequestHeader(value = "USER-ID") @NotNull UUID userId,
            @Schema(description = "The afterDate of the order in ISO-8601 format", example = "2021-01-01T00:00:00Z")
            @RequestParam(value = "afterDate") @NotNull Instant afterDate,
            @Schema(description = "The beforeDate of the order in ISO-8601 format", example = "2021-12-31T23:59:59Z")
            @RequestParam(value = "beforeDate") @NotNull Instant beforeDate) {
        return ApiResponse.ok(orderService.getTotalPriceByUserIdAndDateRange(userId, afterDate, beforeDate));
    }

    @Operation(summary = "Get all orders for a specific user", description = "Get all orders for a specific user.")
    @GetMapping(name = "GetUserOrders", produces = APPLICATION_JSON_VALUE)
    ApiResponse<Page<OrderDto>> getUserOrders(
            @RequestHeader(value = "USER-ID") @NotNull UUID userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ApiResponse.ok(orderService.getOrdersByUserId(userId, PageRequest.of(page, size)));
    }

    @Operation(summary = "Get order", description = "Get order by id.")
    @GetMapping(name = "GetOrder", value = "/{id}", produces = APPLICATION_JSON_VALUE)
    ApiResponse<OrderDto> getOrderById(@PathVariable("id") @NotNull UUID id) {
        return ApiResponse.ok(orderService.getOrder(id));
    }

    @Operation(summary = "Delete order", description = "Delete order by id.")
    @DeleteMapping(name = "DeleteOrder", value = "/{id}")
    ApiResponse<Void> deleteOrder(@PathVariable("id") @NotNull UUID id) {
        orderService.deleteOrder(id);
        return ApiResponse.ok();
    }

}
