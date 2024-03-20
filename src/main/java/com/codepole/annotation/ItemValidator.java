package com.codepole.annotation;

import com.codepole.dto.in.OrderItemDto;
import com.codepole.repository.ItemRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class ItemValidator implements ConstraintValidator<HasValidItems, List<OrderItemDto>> {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public boolean isValid(List<OrderItemDto> orderItems, ConstraintValidatorContext context) {
        try {
            if(orderItems == null || orderItems.isEmpty()) {
                return true;
            }
            var itemIds = orderItems.stream().map(OrderItemDto::getItemId).distinct().toList();
            var items = itemRepository.findDistinctByIdIn(itemIds);
            if (itemIds.size() == items.size()) {
                return true;
            }
        } catch (Exception e) {
            log.error("Fatal error while validating items: {}", e.getMessage());
        }
        return false;
    }
}
