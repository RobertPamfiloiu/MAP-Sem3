package model.statement;

import exception.MyException;
import model.state.PrgState;
import model.type.Type;
import model.adt.MyIDictionary;

/**
 * This is the interface for all statements.
 * The 'execute' method is the core of the interpreter. It defines
 * what a statement does to the PrgState.
 */
public interface IStmt {
    PrgState execute(PrgState state) throws MyException;
    MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException;
}