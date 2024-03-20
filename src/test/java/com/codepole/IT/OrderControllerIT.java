package com.codepole.IT;

import com.codepole.BaseIntegrationTest;
import com.codepole.domain.Item;
import com.codepole.domain.Order;
import com.codepole.domain.OrderItem;
import com.codepole.domain.OrderItemId;
import com.codepole.dto.in.CreateOrderDto;
import com.codepole.dto.in.OrderItemDto;
import com.codepole.dto.in.UpdateOrderDto;
import com.codepole.repository.ItemRepository;
import com.codepole.repository.OrderRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerIT extends BaseIntegrationTest {

    private static final String BASE_PATH_V1_ORDER = "/api/v1/orders";
    private static final String BASE_PATH_V1_ORDER_TOTAL_PRICE = BASE_PATH_V1_ORDER + "/total-price";
    private static final String BASE_PATH_V1_ORDER_BY_ID = BASE_PATH_V1_ORDER + "/{id}";
    private static final String REQUEST_HEADER_USER_ID = "USER-ID";
    private static final String REQUEST_PARAM_BEFORE_DATE = "beforeDate";
    private static final String REQUEST_PARAM_AFTER_DATE = "afterDate";
    private static final String ITEM_NAME = "TEST_ITEM";
    private static final BigDecimal UNIT_PRICE = BigDecimal.TEN;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @SneakyThrows
    void shouldCreateOrderAndReturnId() {
        // Given
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var createOrderDto = CreateOrderDto.builder()
                .orderItemDtos(Collections.singletonList(
                        OrderItemDto.builder().itemId(item.getId()).amount(1).build())).build();

        // When
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ORDER))
                        .header(REQUEST_HEADER_USER_ID, UUID.randomUUID().toString())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createOrderDto)));

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())));
    }

    @Test
    @SneakyThrows
    void shouldRespondBadRequestWhenCreateOrderWithoutValidUserUuid() {
        // Given
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var createOrderDto = CreateOrderDto.builder()
                .orderItemDtos(Collections.singletonList(
                        OrderItemDto.builder().itemId(item.getId()).amount(1).build())).build();

        // When
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ORDER))
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createOrderDto)));

        // Then bad request since userId is required.
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.value())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithUserOrders() {
        // Given
        var userId = UUID.randomUUID();
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var order = orderRepository.save(Order.builder().userId(userId).build());
        var orderItem = OrderItem.builder()
                .id(OrderItemId.builder()
                        .orderId(order.getId())
                        .itemId(item.getId())
                        .build())
                .order(order)
                .item(item)
                .amount(10)
                .totalPrice(BigDecimal.TEN)
                .build();
        order.addOrderItem(orderItem);
        orderRepository.save(order);

        // When
            var response = mvc.perform(get(getEndpointUrl(BASE_PATH_V1_ORDER))
                    .header(REQUEST_HEADER_USER_ID, userId.toString())
                    .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())))
                .andExpect(jsonPath("$.response.content", hasSize(1)))
                .andExpect(jsonPath("$.response.content[0].id", is(order.getId().toString())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithOrderWhenLookingForOrderById() {
        // Given
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var order = orderRepository.save(Order.builder().userId(UUID.randomUUID()).build());
        var orderItem = OrderItem.builder()
                .id(OrderItemId.builder()
                        .orderId(order.getId())
                        .itemId(item.getId())
                        .build())
                .order(order)
                .item(item)
                .amount(10)
                .totalPrice(BigDecimal.TEN)
                .build();
        order.addOrderItem(orderItem);
        orderRepository.save(order);

        // When
        var response = mvc.perform(get(getEndpointUrl(BASE_PATH_V1_ORDER_BY_ID), order.getId())
                .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())))
                .andExpect(jsonPath("$.response.id", is(order.getId().toString())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForNonExistingOrderById() {
        // When
        var response = mvc.perform(get(getEndpointUrl(BASE_PATH_V1_ORDER_BY_ID),
                UUID.randomUUID())
                .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    @SneakyThrows
    void shouldReturnUserOrderTotalPriceWith10PercentDiscount() {
        // Given
        var userId = UUID.randomUUID();
        var afterDate = Instant.now();
        var beforeDate = afterDate.plus(2, ChronoUnit.DAYS);
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var order = orderRepository.save(Order.builder()
                .userId(userId)
                .date(afterDate.plus(1, ChronoUnit.DAYS)).build());
        var orderItem = OrderItem.builder()
                .id(OrderItemId.builder()
                        .orderId(order.getId())
                        .itemId(item.getId())
                        .build())
                .order(order)
                .item(item)
                .amount(10)
                .totalPrice(BigDecimal.valueOf(90))
                .build();
        order.addOrderItem(orderItem);
        orderRepository.save(order);

        // When
        var response = mvc.perform(get(getEndpointUrl(BASE_PATH_V1_ORDER_TOTAL_PRICE))
                .header(REQUEST_HEADER_USER_ID, userId)
                .queryParam(REQUEST_PARAM_BEFORE_DATE, beforeDate.toString())
                .queryParam(REQUEST_PARAM_AFTER_DATE, afterDate.toString())
                .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())))
                .andExpect(jsonPath("$.response", is(90)));
    }

    @Test
    @SneakyThrows
    void shouldDeleteOrderByIdSuccessfully() {
        //Given
        var order = orderRepository.save(Order.builder().userId(UUID.randomUUID()).build());

        // When
        var response = mvc.perform(delete(getEndpointUrl(BASE_PATH_V1_ORDER_BY_ID), order.getId())
                .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is2xxSuccessful());
    }

    @Test
    @SneakyThrows
    void shouldRespondWithBadRequestWhenDeletingOrderWhereIdIsNotValidUUID() {
        // When
        var response = mvc.perform(delete(getEndpointUrl(BASE_PATH_V1_ORDER_BY_ID), "INVALID_UUID")
                .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void shouldRespondWithBadRequestWhenCreatingOrderWithNonExistingItem() {
        // Given
        var createOrderDto = CreateOrderDto.builder()
                .orderItemDtos(Collections.singletonList(
                        OrderItemDto.builder().itemId(UUID.randomUUID()).amount(1).build())).build();

        // When
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ORDER))
                        .header(REQUEST_HEADER_USER_ID, UUID.randomUUID().toString())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createOrderDto)));

        // Then bad request since title is required.
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.detail", is("Validation failure")));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithBadRequestWhenCreatingOrderWithZeroAmount() {
        // Given
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var createOrderDto = CreateOrderDto.builder()
                .orderItemDtos(Collections.singletonList(
                        OrderItemDto.builder().itemId(item.getId()).amount(0).build())).build();

        // When
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ORDER))
                        .header(REQUEST_HEADER_USER_ID, UUID.randomUUID().toString())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createOrderDto)));


        // Then bad request since price must be > 0.
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.detail", is("Validation failure")));
    }

    @Test
    @SneakyThrows
    void shouldUpdateExistingOrderSuccessfully() {
        // Given
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var order = orderRepository.save(Order.builder().userId(UUID.randomUUID()).build());
        var orderItem = OrderItem.builder()
                .id(OrderItemId.builder()
                        .orderId(order.getId())
                        .itemId(item.getId())
                        .build())
                .order(order)
                .item(item)
                .amount(10)
                .totalPrice(BigDecimal.TEN)
                .build();
        order.addOrderItem(orderItem);
        orderRepository.save(order);

        // When
        var updateOrderDto = UpdateOrderDto.builder()
                .orderId(order.getId())
                .orderItemDtos(Collections.singletonList(
                        OrderItemDto.builder().itemId(item.getId()).amount(10).build())).build();
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ORDER))
                        .header(REQUEST_HEADER_USER_ID, UUID.randomUUID().toString())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateOrderDto)));

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status_code", is(OK.value())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithNotFoundWhenUpdatingNonExistingOrder() {
        // Given a random id that should not exist should result in a HTTP404
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());
        var updateOrderDto = UpdateOrderDto.builder()
                .orderId(UUID.randomUUID())
                .orderItemDtos(Collections.singletonList(
                        OrderItemDto.builder().itemId(item.getId()).amount(10).build())).build();

        // When
        var response = mvc.perform(
                put(getEndpointUrl(BASE_PATH_V1_ORDER))
                        .header(REQUEST_HEADER_USER_ID, UUID.randomUUID().toString())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateOrderDto)));

        // Then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

}
