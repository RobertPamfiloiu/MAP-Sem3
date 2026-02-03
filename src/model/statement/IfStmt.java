package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIStack;
import model.expression.Exp;
import model.state.PrgState;
import model.type.BoolType;
import model.value.BoolValue;
import model.value.Value;
import model.type.Type;
import model.adt.MyIDictionary;

/**
 * Represents an If statement (e.g. If exp Then Stmt1 Else Stmt2).
 */
public class IfStmt implements IStmt {
    private final Exp exp;
    private final IStmt thenS;
    private final IStmt elseS;

    public IfStmt(Exp e, IStmt t, IStmt el) {
        exp = e;
        thenS = t;
        elseS = el;
    }

    @Override
    public String toString() {
        return "(IF(" + exp.toString() + ") THEN(" + thenS.toString()
                + ")ELSE(" + elseS.toString() + "))";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {

        MyIStack<IStmt> stack = state.getStk();
        Value cond = exp.eval(state.getSymTable(), state.getHeap());

        if (cond.getType().equals(new BoolType())) {
            boolean condValue = ((BoolValue) cond).getVal();

            if (condValue) {
                stack.push(thenS);
            } else {
                stack.push(elseS);
            }
        } else {
            throw new StatementException("conditional expression is not a boolean");
        }

        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typexp = exp.typecheck(typeEnv);
        if (typexp.equals(new BoolType())) {
            // We clone the environment for the branches because variables declared inside
            // an IF block (in most scopes) should not affect the outer scope or the other branch.
            thenS.typecheck(typeEnv.deepCopy());
            elseS.typecheck(typeEnv.deepCopy());
            return typeEnv;
        } else {
            throw new MyException("The condition of IF isn't of the type bool");
        }
    }
}