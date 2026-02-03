package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.expression.Exp;
import model.state.PrgState;
import model.type.RefType;
import model.value.RefValue;
import model.value.Value;
import model.type.Type;

/**
 * Represents a 'new' statement that allocates memory on the heap
 * and assigns a reference to a variable.
 */

public class NewStmt implements IStmt {
    private String varName;
    private Exp exp;

    public NewStmt(String varName, Exp exp) {
        this.varName = varName;
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTable = state.getSymTable();
        MyIHeap<Value> heap = state.getHeap();

        if (!symTable.isDefined(varName)) {
            throw new StatementException("Variable " + varName + " is not defined");
        }

        Value varValue = symTable.lookup(varName);
        if (!(varValue.getType() instanceof RefType)) {
            throw new StatementException("Variable " + varName + " is not of type RefType");
        }

        Value evalValue = exp.eval(symTable, heap);
        RefType refType = (RefType) varValue.getType();

        // Check types: inner type of variable vs type of expression
        if (!evalValue.getType().equals(refType.getInner())) {
            throw new StatementException("Type mismatch: expected " + refType.getInner() +
                    " but got " + evalValue.getType());
        }

        // Allocate in heap and update symbol table
        int newAddress = heap.allocate(evalValue);
        symTable.update(varName, new RefValue(newAddress, refType.getInner()));

        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typevar = typeEnv.lookup(varName);
        Type typexp = exp.typecheck(typeEnv);
        if (typevar.equals(new RefType(typexp))) {
            return typeEnv;
        } else {
            throw new MyException("NEW stmt: right hand side and left hand side have different types");
        }
    }

    @Override
    public String toString() { return "new(" + varName + ", " + exp.toString() + ")"; }
}