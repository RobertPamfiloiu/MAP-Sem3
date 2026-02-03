package model.adt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MyList<T> implements MyIList<T> {

    private List<T> list;

    public MyList() {
        this.list = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void add(T v) {
        this.list.add(v);
    }

    @Override
    public List<T> getList() {
        return list;
    }

    @Override
    public String toString() {
        return list.stream()
                .map(T::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public List<T> getContent() {
        return list;
    }
}