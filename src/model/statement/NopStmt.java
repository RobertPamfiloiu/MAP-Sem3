package model.statement;

import model.type.Type;
import model.adt.MyIDictionary;
import exception.MyException;
import model.state.PrgState;

/**
 * Represents a No-Operation statement. It does nothing.
 */
public class NopStmt implements IStmt {

    @Override
    public PrgState execute(PrgState state) throws MyException {
        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        // NOP doesn't change types or introduce variables, so return env as is
        return typeEnv;
    }

    @Override
    public String toString() {
        return "nop";
    }
}