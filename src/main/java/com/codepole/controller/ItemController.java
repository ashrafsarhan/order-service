package com.codepole.controller;

import com.codepole.api.ApiResponse;
import com.codepole.dto.in.CreateItemDto;
import com.codepole.dto.in.UpdateItemDto;
import com.codepole.dto.out.ItemDto;
import com.codepole.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/items")
@Tag(name = "Item", description = "Item API manages the items.")
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Create item", description = "Create a new item entity with name and unit price.")
    @PostMapping(name = "CreateItem", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ApiResponse<UUID> createItem(
            @RequestBody @Valid CreateItemDto createItemDto) {
        var itemId = itemService.createItem(createItemDto);
        return ApiResponse.ok(itemId);
    }

    @Operation(summary = "Update item", description = "Update item entity with name and unit price.")
    @PutMapping(name = "UpdateItem", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ApiResponse<UUID> updateItem(
            @RequestBody @Valid UpdateItemDto updateItemDto) {
        var itemId = itemService.updateItem(updateItemDto);
        return ApiResponse.ok(itemId);
    }

    @Operation(summary = "Get all items", description = "Get all items")
    @GetMapping(name = "GetAllItems", produces = APPLICATION_JSON_VALUE)
    ApiResponse<Page<ItemDto>> getItems(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ApiResponse.ok(itemService.getItems(PageRequest.of(page, size)));
    }

    @Operation(summary = "Get item", description = "Get item by id.")
    @GetMapping(name = "GetItem", value = "/{id}", produces = APPLICATION_JSON_VALUE)
    ApiResponse<ItemDto> getItemById(@PathVariable("id") @NotNull UUID id) {
        return ApiResponse.ok(itemService.getItem(id));
    }

    @Operation(summary = "Delete item", description = "Delete item by id.")
    @DeleteMapping(name = "DeleteItem", value = "/{id}")
    ApiResponse<Void> deleteItem(@PathVariable("id") @NotNull UUID id) {
        itemService.deleteItem(id);
        return ApiResponse.ok();
    }

}
