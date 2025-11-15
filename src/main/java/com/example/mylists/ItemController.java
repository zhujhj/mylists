package com.example.mylists;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository repo;

    public ItemController(ItemRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Item create(@RequestBody CreateItemRequest req) {
        if (req.getListType() == null || req.getListType().isBlank()
                || req.getText() == null || req.getText().isBlank()) {
            throw new IllegalArgumentException("listType and text are required");
        }

        Item item = new Item();
        item.setListType(req.getListType());
        item.setText(req.getText());
        item.setCompleted(false);

        return repo.save(item);
    }

    @GetMapping
    public List<Item> list(@RequestParam String listType) {
        return repo.findByListTypeOrderByCreatedAtDesc(listType);
    }
}
