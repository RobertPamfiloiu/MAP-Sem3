package model.statement;

import exception.MyException;
import model.adt.MyDictionary;
import model.adt.MyStack;
import model.state.PrgState;
import model.adt.MyIDictionary;
import model.type.Type;

public class ForkStmt implements IStmt {
    private IStmt statement;

    public ForkStmt(IStmt statement) {
        this.statement = statement;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        // Create new Stack
        // Clone SymTable
        // Share Heap, FileTable, Out

        return new PrgState(
                new MyStack<>(),
                state.getSymTable().deepCopy(),
                state.getOut(),
                state.getFileTable(),
                state.getHeap(),
                statement
        );
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        statement.typecheck(typeEnv.deepCopy());
        return typeEnv;
    }

    @Override
    public String toString() {
        return "fork(" + statement.toString() + ")";
    }
}