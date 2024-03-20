package com.codepole.IT;

import com.codepole.BaseIntegrationTest;
import com.codepole.domain.Item;
import com.codepole.dto.in.CreateItemDto;
import com.codepole.dto.in.UpdateItemDto;
import com.codepole.repository.ItemRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
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

class ItemControllerIT extends BaseIntegrationTest {

    private static final String BASE_PATH_V1_ITEM = "/api/v1/items";
    private static final String BASE_PATH_V1_ITEM_BY_ID = BASE_PATH_V1_ITEM + "/{id}";
    private static final String ITEM_NAME = "TEST_ITEM";
    private static final BigDecimal UNIT_PRICE = BigDecimal.TEN;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @SneakyThrows
    void shouldCreateItemAndReturnId() {
        // Given
        var createItemDto = CreateItemDto.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build();

        // When
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ITEM))
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto)));

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithAllItems() {
        //Given
        var item = Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build();
        itemRepository.save(item);

        // When
        var response = mvc.perform(get(getEndpointUrl(BASE_PATH_V1_ITEM)).contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.response", is(notNullValue())))
                .andExpect(jsonPath("$.response.content", hasSize(1)))
                .andExpect(jsonPath("$.response.content[0].id", is(item.getId().toString())))
                .andExpect(jsonPath("$.response.content[0].name", is(ITEM_NAME)));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithFoundWhenSearchingForNonExistingItemById() {
        // When
        var requestUrl = getEndpointUrl(BASE_PATH_V1_ITEM_BY_ID);

        var response = mvc.perform(get(requestUrl, UUID.randomUUID()).contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    @SneakyThrows
    void shouldDeleteItemByIdSuccessfully() {
        //Given
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());

        // When
        var response = mvc.perform(delete(getEndpointUrl(BASE_PATH_V1_ITEM_BY_ID), item.getId())
                .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is2xxSuccessful());
    }

    @Test
    @SneakyThrows
    void shouldRespondWithBadRequestWhenDeletingItemWhereIdIsNotValidUUID() {
        // When
        var response = mvc.perform(delete(getEndpointUrl(BASE_PATH_V1_ITEM_BY_ID), "INVALID_UUID")
                .contentType(APPLICATION_JSON));

        // Then
        response.andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void shouldRespondWithBadRequestWhenCreatingItemWithNoName() {
        // Given
        var createItemDto = CreateItemDto.builder().unitPrice(UNIT_PRICE).build();

        // When
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ITEM))
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto)));

        // Then bad request since title is required.
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithBadRequestWhenCreatingItemWithZeroPrice() {
        // Given
        var createItemDto = CreateItemDto.builder().name(ITEM_NAME).unitPrice(BigDecimal.ZERO).build();

        // When
        var response = mvc.perform(
                post(getEndpointUrl(BASE_PATH_V1_ITEM))
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createItemDto)));

        // Then bad request since price must be > 0.
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")));
    }

    @Test
    @SneakyThrows
    void shouldUpdateExistingItemSuccessfully() {
        //Given
        var item = itemRepository.save(Item.builder().name(ITEM_NAME).unitPrice(UNIT_PRICE).build());

        // When
        var updatedItem = UpdateItemDto.builder()
                .id(item.getId())
                .name("UPDATED_ITEM")
                .unitPrice(UNIT_PRICE).build();
        var response = mvc.perform(
                put(getEndpointUrl(BASE_PATH_V1_ITEM))
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedItem)));
        // Then
        response.andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status_code", is(OK.value())));
    }

    @Test
    @SneakyThrows
    void shouldRespondWithNotFoundWhenUpdatingNonExistingItem() {
        // Given a random isbn that should not exist should result in a HTTP404
        var updatedItem = UpdateItemDto.builder()
                .id(UUID.randomUUID())
                .name(ITEM_NAME)
                .unitPrice(UNIT_PRICE).build();

        // When
        var response = mvc.perform(
                put(getEndpointUrl(BASE_PATH_V1_ITEM))
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedItem)));
        // Then
        response.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(NOT_FOUND.value())))
                .andExpect(jsonPath("$.title", is("Not Found")));
    }

}
