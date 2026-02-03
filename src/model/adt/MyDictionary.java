package model.adt;

import exception.AdtException;
import exception.MyException;
import java.util.HashMap;
import java.util.Map;

public class MyDictionary<K, V> implements MyIDictionary<K, V> {
    private Map<K, V> dictionary;

    public MyDictionary() {
        this.dictionary = new HashMap<>();
    }

    @Override
    public boolean isDefined(K key) { return dictionary.containsKey(key); }

    @Override
    public V lookup(K key) throws MyException {
        if (!isDefined(key)) throw new AdtException("Key " + key + " is not defined.");
        return dictionary.get(key);
    }

    @Override
    public void put(K key, V value) { dictionary.put(key, value); }

    @Override
    public void update(K key, V value) throws MyException {
        if (!isDefined(key)) throw new AdtException("Key " + key + " is not defined.");
        dictionary.put(key, value);
    }

    @Override
    public void remove(K key) throws MyException {
        if (!isDefined(key)) throw new AdtException("Key " + key + " is not defined.");
        dictionary.remove(key);
    }

    @Override
    public Map<K, V> getContent() { return dictionary; }

    @Override
    public String toString() { return dictionary.toString(); }

    // Implementation for Concurrency
    @Override
    public MyIDictionary<K, V> deepCopy() {
        MyDictionary<K, V> toReturn = new MyDictionary<>();
        for (Map.Entry<K, V> entry : dictionary.entrySet()) {
            toReturn.put(entry.getKey(), entry.getValue());
        }
        return toReturn;
    }
}