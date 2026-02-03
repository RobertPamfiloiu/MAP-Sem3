package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIStack;
import model.expression.Exp;
import model.state.PrgState;
import model.type.BoolType;
import model.value.BoolValue;
import model.value.Value;
import model.adt.MyIDictionary;
import model.type.Type;

public class WhileStmt implements IStmt {
    private Exp exp;
    private IStmt statement;

    public WhileStmt(Exp exp, IStmt statement) {
        this.exp = exp;
        this.statement = statement;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        Value val = exp.eval(state.getSymTable(), state.getHeap());
        if (!val.getType().equals(new BoolType())) {
            throw new StatementException("Condition expression is not a boolean");
        }

        BoolValue condition = (BoolValue) val;
        if (condition.getVal()) {
            // Push the while statement back onto the stack
            MyIStack<IStmt> stack = state.getStk();
            stack.push(this); // Push the while loop again
            stack.push(statement); // Push the body to execute
        }
        // If false, do nothing (loop terminates)

        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typexp = exp.typecheck(typeEnv);
        if (typexp.equals(new BoolType())) {
            statement.typecheck(typeEnv.deepCopy());
            return typeEnv;
        } else {
            throw new MyException("The condition of WHILE is not of the type bool");
        }
    }

    @Override
    public String toString() { return "while(" + exp.toString() + ") " + statement.toString(); }
}