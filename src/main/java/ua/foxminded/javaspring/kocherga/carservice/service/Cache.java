package ua.foxminded.javaspring.kocherga.carservice.service;

import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> {

    private final Map<K, V> CACHE = new HashMap<>();

    public V getValue(K key) {
        return CACHE.get(key);
    }

    public void putValue(K key, V value) {
        CACHE.put(key, value);
    }
}
