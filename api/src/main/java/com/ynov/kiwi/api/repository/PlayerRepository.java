package com.ynov.kiwi.api.repository;

import com.ynov.kiwi.api.entity.Player;

import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

@Repository
public class PlayerRepository {
    private final Map<Integer, Player> store = new ConcurrentHashMap<>();

    public Player save(Player player) {
        store.put(player.getId(), player);
        return player;
    }
    public Optional<Player> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }
    public Collection<Player> findAll() { return store.values(); }
    public void clear() { store.clear(); }
    public boolean existsById(int id) { return store.containsKey(id); }
}
