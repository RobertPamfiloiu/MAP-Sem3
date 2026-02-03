package model.adt;

import exception.MyException;
import java.util.Map;

public interface MyIDictionary<K, V> {
    boolean isDefined(K key);
    V lookup(K key) throws MyException;
    void put(K key, V value);
    void update(K key, V value) throws MyException;
    void remove(K key) throws MyException;
    Map<K, V> getContent();

    MyIDictionary<K, V> deepCopy();
}