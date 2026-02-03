package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.expression.Exp;
import model.state.PrgState;
import model.type.Type;
import model.value.Value;
/**
 * Represents an assignment statement (e.g., v = 2).
 */
public class AssignStmt implements IStmt {
    private final String id;
    private final Exp exp;

    public AssignStmt(String id, Exp exp) {
        this.id = id;
        this.exp = exp;
    }

    @Override
    public String toString() {
        return id + "=" + exp.toString();
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTbl = state.getSymTable();
        MyIHeap<Value> heap = state.getHeap();

        if (symTbl.isDefined(id)) {
            Value val = exp.eval(symTbl, heap);
            Type typId = (symTbl.lookup(id)).getType();

            if ((val.getType()).equals(typId)) {
                symTbl.update(id, val);
            } else
                throw new StatementException("declared type of variable " + id + " and type of the assigned expression do not match");
        } else
            throw new StatementException("the used variable " + id + " was not declared before");

        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typevar = typeEnv.lookup(id);
        Type typexp = exp.typecheck(typeEnv);
        if (typevar.equals(typexp))
            return typeEnv;
        else
            throw new MyException("Assignment: right hand side and left hand side have different types");
    }
}