package model.type;

import model.value.Value;

public interface Type {
    /**
        We add this method to get the default value for a type
        (e.g. 0 for int, false for bool).
     */
    Value defaultValue();
}