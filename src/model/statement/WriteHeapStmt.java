package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.expression.Exp;
import model.state.PrgState;
import model.value.RefValue;
import model.value.Value;
import model.type.RefType;
import model.type.Type;

/**
 * Represents a statement that writes a value to a heap location
 * referenced by a variable.
 */

public class WriteHeapStmt implements IStmt {
    private String varName;
    private Exp exp;

    public WriteHeapStmt(String varName, Exp exp) {
        this.varName = varName;
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTable = state.getSymTable();
        MyIHeap<Value> heap = state.getHeap();

        if (!symTable.isDefined(varName)) {
            throw new StatementException("Variable " + varName + " not defined");
        }

        Value val = symTable.lookup(varName);
        if (!(val instanceof RefValue)) {
            throw new StatementException("Variable " + varName + " is not a reference");
        }

        RefValue refVal = (RefValue) val;
        if (!heap.containsKey(refVal.getAddr())) {
            throw new StatementException("Address " + refVal.getAddr() + " not defined in heap");
        }

        Value evalVal = exp.eval(symTable, heap);
        if (!evalVal.getType().equals(refVal.getLocationType())) {
            throw new StatementException("Type mismatch for heap update");
        }

        heap.put(refVal.getAddr(), evalVal);
        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typevar = typeEnv.lookup(varName);
        Type typexp = exp.typecheck(typeEnv);

        if (typevar.equals(new RefType(typexp))) {
            return typeEnv;
        } else {
            throw new MyException("WriteHeap stmt: right hand side and left hand side have different types");
        }
    }

    @Override
    public String toString() { return "wH(" + varName + ", " + exp.toString() + ")"; }
}