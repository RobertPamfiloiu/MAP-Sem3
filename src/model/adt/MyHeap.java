package model.adt;

import exception.AdtException;
import exception.MyException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

public class MyHeap<V> implements MyIHeap<V> {
    private Map<Integer, V> map;
    private AtomicInteger freeLocation;

    public MyHeap() {
        this.map = new ConcurrentHashMap<>();
        this.freeLocation = new AtomicInteger(1);
    }

    @Override
    public int allocate(V value) {
        int newId = freeLocation.getAndIncrement();
        map.put(newId, value);
        return newId;
    }

    @Override
    public V get(int address) throws MyException {
        if (!map.containsKey(address)) {
            throw new AdtException("Heap address " + address + " is not defined.");
        }
        return map.get(address);
    }

    @Override
    public void put(int address, V value) {
        map.put(address, value);
    }

    @Override
    public boolean containsKey(int address) {
        return map.containsKey(address);
    }

    @Override
    public void setContent(Map<Integer, V> content) {
        this.map = new ConcurrentHashMap<>(content);
    }

    @Override
    public Map<Integer, V> getContent() {
        return map;
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                .map(e -> e.getKey() + " -> " + e.getValue())
                .collect(Collectors.joining("\n"));
    }
}