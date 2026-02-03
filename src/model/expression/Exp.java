package model.expression;

import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.type.Type;
import model.value.Value;

public interface Exp {
    /**
     * Evaluates the expression using the symbol table.
     * @param tbl The symbol table.
     * @return The resulting Value of the evaluation.
     */
    Value eval(MyIDictionary<String, Value> tbl, MyIHeap<Value> heap) throws MyException;

    Type typecheck(MyIDictionary<String, Type> typeEnv) throws MyException;
}