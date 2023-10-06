package ru.practicum.shareit.item.storage;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceprion.NotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@Component
public class InMemoryItemStorage implements ItemStorage {
    private final HashMap<Integer, Set<Item>> storage;
    private Integer itemId = 1;

    @Override
    public Item save(Item i) {
        if (storage.containsKey(i.getOwner())) {
            i.setId(provideId());
            storage.get(i.getOwner()).add(i);
            return i;
        } else {
            throw new NotFoundException(String.format("There is no user with id = %s." +
                    " Item cannot be added to unknown user", i.getOwner()));
        }
    }

    @Override
    public Item update(Item i, Integer itemId) {
        if (!storage.containsKey(i.getOwner())) {
            throw new NotFoundException(String.format("There is no such user with id = %s. " +
                    "Item cannot be updated by unknown user", i.getOwner()));
        }
        if (!containsItem(i.getOwner(), itemId)) {
            throw new NotFoundException(String.format("There is no with id = %s. " +
                    "Item cannot be updated", itemId));
        }
        Item item = getItemById(itemId);
        if (i.getName() != null) {
            item.setName(i.getName());
        }
        if (i.getDescription() != null) {
            item.setDescription(i.getDescription());
        }
        if (i.getAvailable() != null) {
            item.setAvailable(i.getAvailable());
        }
        return item;
    }

    @Override
    public Item getItemById(Integer id) {
        return storage.values()
                .stream()
                .flatMap(Set::stream)
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException(String.format("Failed to find item with id = %s", id)));
    }

    @Override
    public Set<Item> getItemsByUser(Integer userId) {
        if (storage.containsKey(userId)) {
            return storage.get(userId);
        } else {
            throw new NotFoundException(String.format("User with an id = %s not found", userId));
        }
    }

    @Override
    public List<Item> getAllByText(String text) {
        return storage.values()
                .stream()
                .flatMap(Set::stream)
                .filter(item -> item.getAvailable() &&
                        (item.getDescription().toLowerCase().contains(text.toLowerCase()) ||
                                item.getName().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public void addUser(Integer id) {
        storage.put(id, new HashSet<>());
    }

    private Integer provideId() {
        return this.itemId++;
    }

    private boolean containsItem(Integer usId, Integer itId) {
        for (Item it : storage.get(usId)) {
            if (it.getId().equals(itId)) {
                return true;
            }
        }
        return false;
    }
}