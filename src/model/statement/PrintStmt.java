package model.statement;

import exception.MyException;
import model.adt.MyIList;
import model.expression.Exp;
import model.state.PrgState;
import model.value.Value;
import model.type.Type;
import model.adt.MyIDictionary;

/**
 * Represents a print statement (e.g. Print(v)).
 */
public class PrintStmt implements IStmt {
    private final Exp exp;

    public PrintStmt(Exp exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "print(" + exp.toString() + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIList<Value> out = state.getOut();
        Value val = exp.eval(state.getSymTable(), state.getHeap());
        out.add(val);
        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        exp.typecheck(typeEnv);
        return typeEnv;
    }
}