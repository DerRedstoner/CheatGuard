package de.derredstoner.anticheat.util.evicting;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class EvictingMap<K, V> extends HashMap<K, V> {

    private final int size;
    private final Deque<K> storedKeys = new LinkedList<>();

    public EvictingMap(final int size) {
        this.size = size;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if(!storedKeys.contains(key) || !get(key).equals(value)) {
            checkAndRemove();
        }
        return super.putIfAbsent(key, value);
    }

    @Override
    public V put(K key, V value) {
        checkAndRemove();
        storedKeys.addLast(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        storedKeys.clear();
        super.clear();
    }

    @Override
    public V remove(Object key) {
        storedKeys.remove(key);
        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        storedKeys.remove(key);
        return super.remove(key, value);
    }

    private boolean checkAndRemove() {
        if(storedKeys.size() >= size) {
            final K key = storedKeys.removeFirst();

            remove(key);
            return true;
        }
        return false;
    }

}
