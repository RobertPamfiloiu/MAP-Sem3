package model.adt;

import exception.MyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class MyStack<T> implements MyIStack<T> {
    private Stack<T> stack;

    public MyStack() {
        this.stack = new Stack<>();
    }

    @Override
    public T pop() throws MyException {
        if (stack.isEmpty()) throw new MyException("Stack is empty");
        return stack.pop();
    }

    @Override
    public void push(T v) {
        stack.push(v);
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public List<T> getReversedStack() {
        synchronized (stack) {
            List<T> list = Arrays.asList((T[]) stack.toArray());
            Collections.reverse(list);
            return list;
        }
    }

    @Override
    public String toString() {
        synchronized (stack) {
            return stack.toString();
        }
    }
}