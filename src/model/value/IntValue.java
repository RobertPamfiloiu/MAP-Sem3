package model.value;

import model.type.IntType;
import model.type.Type;

public class IntValue implements Value {
    private final int val;

    public IntValue(int v) {
        this.val = v;
    }

    public int getVal() {
        return this.val;
    }

    @Override
    public String toString() {
        return String.valueOf(this.val);
    }

    @Override
    public Type getType() {
        return new IntType();
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (another == null || getClass() != another.getClass()) return false;
        IntValue that = (IntValue) another;
        return val == that.val;
    }
}