package com.codepole.repository;

import com.codepole.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findDistinctByIdIn(List<UUID> itemIds);
}
