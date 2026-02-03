package model.adt;

import exception.MyException;
import java.util.Map;


public interface MyIHeap<V> {
    int allocate(V value); // Generates a new address, stores value, returns address
    V get(int address) throws MyException;
    void put(int address, V value);
    boolean containsKey(int address);
    void setContent(Map<Integer, V> content);
    Map<Integer, V> getContent();
}