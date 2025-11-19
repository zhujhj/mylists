package com.example.mylists;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findByListTypeOrderByCreatedAtDesc(String listType);

    @Query("select distinct i.listType from Item i order by i.listType")
    List<String> findDistinctListTypes();

    Optional<Item> findFirstByListTypeAndTextOrderByCreatedAtDesc(String listType, String text);


}
