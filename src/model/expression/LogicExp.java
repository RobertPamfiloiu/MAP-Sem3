package model.expression;

import exception.ExpressionException;
import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.type.BoolType;
import model.value.BoolValue;
import model.value.Value;
import model.type.Type;

/**
 * Represents a logical expression (e.g., e1 and e2).
 */

public class LogicExp implements Exp {
    private final Exp e1;
    private final Exp e2;
    private final int op; // 1-and, 2-or

    public LogicExp(String op, Exp e1, Exp e2) {
        this.e1 = e1;
        this.e2 = e2;
        if (op.equalsIgnoreCase("and")) this.op = 1;
        else if (op.equalsIgnoreCase("or")) this.op = 2;
        else this.op = 0;
    }

    @Override
    public Value eval(MyIDictionary<String, Value> tbl, MyIHeap<Value> heap) throws MyException {

        Value v1 = e1.eval(tbl, heap);

        if (v1.getType().equals(new BoolType())) {
            Value v2 = e2.eval(tbl, heap);

            if (v2.getType().equals(new BoolType())) {
                BoolValue b1 = (BoolValue) v1;
                BoolValue b2 = (BoolValue) v2;
                boolean n1 = b1.getVal();
                boolean n2 = b2.getVal();

                if (op == 1) { // 'and'
                    return new BoolValue(n1 && n2);
                }
                if (op == 2) { // 'or'
                    return new BoolValue(n1 || n2);
                }
            } else {
                throw new ExpressionException("Operand2 is not a boolean");
            }
        } else {
            throw new ExpressionException("Operand1 is not a boolean");
        }

        return null;
    }

    @Override
    public Type typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typ1 = e1.typecheck(typeEnv);
        Type typ2 = e2.typecheck(typeEnv);

        if (typ1.equals(new BoolType())) {
            if (typ2.equals(new BoolType())) {
                return new BoolType();
            } else {
                throw new MyException("Logic: second operand is not a boolean");
            }
        } else {
            throw new MyException("Logic: first operand is not a boolean");
        }
    }

    @Override
    public String toString() {
        String opStr = (op == 1) ? "and" : "or";
        return e1.toString() + " " + opStr + " " + e2.toString();
    }
}