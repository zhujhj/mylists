package com.example.mylists;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository repo;
    private final ZoneId zoneId = ZoneId.systemDefault();

    public ItemController(ItemRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Item create(@RequestBody CreateItemRequest req) {
        if (req.getListType() == null || req.getListType().isBlank()
                || req.getText() == null || req.getText().isBlank()) {
            throw new IllegalArgumentException("listType and text are required");
        }

        NaturalLanguageParser.ParsedResult parsed =
                NaturalLanguageParser.parse(req.getText(), zoneId);

        Item item = new Item();
        item.setListType(req.getListType());
        item.setText(req.getText());
        item.setDueAt(parsed.dueAt());
        item.setCompleted(false);

        return repo.save(item);
    }

    @GetMapping
    public List<Item> list(@RequestParam String listType) {
        return repo.findByListTypeOrderByCreatedAtDesc(listType);
    }

    @GetMapping("/lists")
    public List<String> listTypes() {
        List<String> types = repo.findDistinctListTypes();
        if (types.isEmpty()) {
            // Optional: some sensible defaults if DB is empty
            types = List.of("todo", "buy", "ideas");
        }
        return types;
    }

    @PatchMapping("/{id}/complete")
    public Item complete(@PathVariable UUID id) {
        Item item = repo.findById(id)
                .orElseThrow();
        item.setCompleted(true);
        return repo.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        repo.deleteById(id);
    }

    @PostMapping("/deleteByText")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByText(@RequestBody DeleteByTextRequest req) {
        repo.findFirstByListTypeAndTextOrderByCreatedAtDesc(
                req.getListType(),
                req.getText()
        ).ifPresent(repo::delete);
    }



}
