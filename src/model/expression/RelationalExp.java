package model.expression;

import exception.ExpressionException;
import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.type.IntType;
import model.value.BoolValue;
import model.value.IntValue;
import model.value.Value;
import model.type.Type;
import model.type.BoolType;

/**
 * Represents a relational expression (e.g., 'a < b', 'x >= y').
 * Its 'eval' method evaluates both operands and applies the relational operator.
 */
public class RelationalExp implements Exp {
    private final Exp e1;
    private final Exp e2;
    private final String op; // "<", "<=", "==", "!=", ">", ">="

    public RelationalExp(String op, Exp e1, Exp e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public Value eval(MyIDictionary<String, Value> tbl, MyIHeap<Value> heap) throws MyException {
        Value v1 = e1.eval(tbl, heap);
        if (!v1.getType().equals(new IntType())) {
            throw new ExpressionException("First operand is not an integer");
        }
        Value v2 = e2.eval(tbl, heap);
        if (!v2.getType().equals(new IntType())) {
            throw new ExpressionException("Second operand is not an integer");
        }

        IntValue i1 = (IntValue) v1;
        IntValue i2 = (IntValue) v2;
        int n1 = i1.getVal();
        int n2 = i2.getVal();

        // Perform the comparison and return a BoolValue
        switch (op) {
            case "<":
                return new BoolValue(n1 < n2);
            case "<=":
                return new BoolValue(n1 <= n2);
            case "==":
                return new BoolValue(n1 == n2);
            case "!=":
                return new BoolValue(n1 != n2);
            case ">":
                return new BoolValue(n1 > n2);
            case ">=":
                return new BoolValue(n1 >= n2);
            default:
                throw new ExpressionException("Invalid relational operator");
        }
    }

    @Override
    public Type typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typ1 = e1.typecheck(typeEnv);
        Type typ2 = e2.typecheck(typeEnv);

        if (typ1.equals(new IntType())) {
            if (typ2.equals(new IntType())) {
                return new BoolType();
            } else {
                throw new MyException("Relational: second operand is not an integer");
            }
        } else {
            throw new MyException("Relational: first operand is not an integer");
        }
    }

    @Override
    public String toString() {
        return e1.toString() + " " + op + " " + e2.toString();
    }
}