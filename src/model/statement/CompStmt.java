package model.statement;

import exception.MyException;
import model.adt.MyIStack;
import model.state.PrgState;
import model.type.Type;
import model.adt.MyIDictionary;

/**
 * Represents a compound statement (e.g., Stmt1; Stmt2).
 * Its job is to push the two statements onto the stack in
 * reverse order (2nd first, then 1st) so they execute in the correct order.
 */
public class CompStmt implements IStmt {
    private final IStmt first;
    private final IStmt snd;

    public CompStmt(IStmt first, IStmt snd) {
        this.first = first;
        this.snd = snd;
    }

    @Override
    public String toString() {
        return "(" + first.toString() + ";" + snd.toString() + ")";
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIStack<IStmt> stk = state.getStk();
        stk.push(snd);
        stk.push(first);
        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        return snd.typecheck(first.typecheck(typeEnv));
    }
}