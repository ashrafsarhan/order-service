package com.codepole.service;

import com.codepole.dto.in.CreateItemDto;
import com.codepole.dto.in.UpdateItemDto;
import com.codepole.dto.out.ItemDto;
import com.codepole.exception.ItemNotFoundException;
import com.codepole.mapper.ObjectMapper;
import com.codepole.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public UUID createItem(CreateItemDto createItemDto) {
        return itemRepository.save(ObjectMapper.toItem(createItemDto)).getId();
    }

    public ItemDto getItem(UUID itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId)
                .map(ObjectMapper::toItemDto)
                .orElseThrow(() -> new ItemNotFoundException(itemId.toString()));
    }

    public Page<ItemDto> getItems(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(ObjectMapper::toItemDto);
    }

    public UUID updateItem(UpdateItemDto updateItemDto) throws ItemNotFoundException {
        return itemRepository.findById(updateItemDto.getId())
                .map(item -> {
                    item.setName(updateItemDto.getName());
                    item.setUnitPrice(updateItemDto.getUnitPrice());
                    return itemRepository.save(item).getId();
                })
                .orElseThrow(() -> new ItemNotFoundException(updateItemDto.getId().toString()));
    }

    public void deleteItem(UUID itemId) throws ItemNotFoundException {
        itemRepository.findById(itemId).ifPresentOrElse(item ->
                        itemRepository.deleteById(itemId),
                () -> {
                    throw new ItemNotFoundException(itemId.toString());
                });
    }

}
