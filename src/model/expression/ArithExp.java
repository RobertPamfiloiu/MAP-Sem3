package model.expression;

import exception.ExpressionException;
import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.type.IntType;
import model.value.IntValue;
import model.value.Value;
import model.type.Type;

/**
 * Represents an arithmetic expression (e.g., e1 + e2).
 */
public class ArithExp implements Exp {
    private final Exp e1;
    private final Exp e2;
    private final int op; // 1-plus, 2-minus, 3-multiply, 4-divide

    public ArithExp(char op, Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
        if (op == '+') this.op = 1;
        else if (op == '-') this.op = 2;
        else if (op == '*') this.op = 3;
        else if (op == '/') this.op = 4;
        else this.op = 0;
    }

    @Override
    public Value eval(MyIDictionary<String, Value> tbl, MyIHeap<Value> heap) throws MyException {
        Value v1, v2;
        v1 = e1.eval(tbl, heap);
        if (v1.getType().equals(new IntType())) {
            v2 = e2.eval(tbl, heap);
            if (v2.getType().equals(new IntType())) {
                IntValue i1 = (IntValue) v1;
                IntValue i2 = (IntValue) v2;
                int n1 = i1.getVal();
                int n2 = i2.getVal();

                if (op == 1) return new IntValue(n1 + n2);
                if (op == 2) return new IntValue(n1 - n2);
                if (op == 3) return new IntValue(n1 * n2);
                if (op == 4) {
                    if (n2 == 0)
                        throw new ExpressionException("division by zero");
                    else return new IntValue(n1 / n2);
                }
            } else
                throw new ExpressionException("second operand is not an integer");
        } else
            throw new ExpressionException("first operand is not an integer");

        return null;
    }
    @Override
    public Type typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typ1 = e1.typecheck(typeEnv);
        Type typ2 = e2.typecheck(typeEnv);

        if (typ1.equals(new IntType())) {
            if (typ2.equals(new IntType())) {
                return new IntType();
            } else {
                throw new MyException("Arithmetic: second operand is not an integer");
            }
        } else {
            throw new MyException("Arithmetic: first operand is not an integer");
        }
    }

    @Override
    public String toString() {
        char opChar = ' ';
        if (op == 1) opChar = '+';
        if (op == 2) opChar = '-';
        if (op == 3) opChar = '*';
        if (op == 4) opChar = '/';
        return e1.toString() + " " + opChar + " " + e2.toString();
    }
}