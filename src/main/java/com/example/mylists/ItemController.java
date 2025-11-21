package com.example.mylists;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemRepository repo;
    private final ZoneId zoneId = ZoneId.systemDefault();

    @Value("${app.api-key}")
    private String apiKey;

    public ItemController(ItemRepository repo) {
        this.repo = repo;
    }

    private void checkApiKey(String header) {
        if (header == null || !header.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key");
        }
    }

    @PostMapping
    public Item create(@RequestHeader("X-API-Key") String header, @RequestBody CreateItemRequest req) {
        checkApiKey(header);
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
    public List<Item> list(@RequestHeader("X-API-Key") String header, @RequestParam String listType) {
        checkApiKey(header);
        return repo.findByListTypeOrderByCreatedAtDesc(listType);
    }

    @GetMapping("/lists")
    public List<String> listTypes(@RequestHeader("X-API-Key") String header) {
        checkApiKey(header);
        List<String> types = repo.findDistinctListTypes();
        if (types.isEmpty()) {
            // Optional: some sensible defaults if DB is empty
            types = List.of("todo", "buy", "ideas");
        }
        return types;
    }

    @PatchMapping("/{id}/complete")
    public Item complete(@RequestHeader("X-API-Key") String header, @PathVariable UUID id) {
        checkApiKey(header);
        Item item = repo.findById(id)
                .orElseThrow();
        item.setCompleted(true);
        return repo.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-API-Key") String header, @PathVariable UUID id) {
        checkApiKey(header);
        repo.deleteById(id);
    }

    @PostMapping("/deleteByText")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByText(@RequestHeader("X-API-Key") String header, @RequestBody DeleteByTextRequest req) {
        checkApiKey(header);
        repo.findFirstByListTypeAndTextOrderByCreatedAtDesc(
                req.getListType(),
                req.getText()
        ).ifPresent(repo::delete);
    }

}
