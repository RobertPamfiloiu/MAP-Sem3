package model.adt;

import java.util.List;

public interface MyIList<T> {
    void add(T v);
    List<T> getList();
    List<T> getContent();
}