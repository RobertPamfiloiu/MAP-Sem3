package model.value;

import model.type.StringType;
import model.type.Type;

public class StringValue implements Value {
    private final String val;

    public StringValue(String v) {
        this.val = v;
    }

    public String getVal() {
        return val;
    }

    @Override
    public Type getType() {
        return new StringType();
    }

    @Override
    public String toString() {
        return "\"" + val + "\"";
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        if (another == null || getClass() != another.getClass()) return false;
        StringValue that = (StringValue) another;
        return val.equals(that.val);
    }
}