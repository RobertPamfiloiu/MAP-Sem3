package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIDictionary;
import model.state.PrgState;
import model.type.Type;
import model.value.Value;

/**
 * Represents a variable declaration statement (e.g. int v;).
 */
public class VarDeclStmt implements IStmt {
    private final String name;
    private final Type typ;

    public VarDeclStmt(String name, Type typ) {
        this.name = name;
        this.typ = typ;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {

        MyIDictionary<String, Value> symTbl = state.getSymTable();

        if (symTbl.isDefined(name)) {
            throw new StatementException("variable " + name + " is already declared");
        } else {
            Value defaultValue = typ.defaultValue();
            symTbl.put(name, defaultValue);
        }

        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        typeEnv.put(name, typ);
        return typeEnv;
    }

    @Override
    public String toString() {
        return typ.toString() + " " + name;
    }
}