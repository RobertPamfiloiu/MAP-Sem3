package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIDictionary;
import model.expression.Exp;
import model.state.PrgState;
import model.type.StringType;
import model.value.StringValue;
import model.value.Value;
import model.type.Type;
import java.io.BufferedReader;
import java.io.IOException;


public class CloseRFile implements IStmt {
    private final Exp exp;

    public CloseRFile(Exp exp) {
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        // Evaluate expression to a string value
        Value val = exp.eval(state.getSymTable(), state.getHeap());
        if (!val.getType().equals(new StringType())) {
            throw new StatementException("File path expression did not evaluate to a string");
        }

        StringValue fileName = (StringValue) val;
        MyIDictionary<StringValue, BufferedReader> fileTable = state.getFileTable();

        // Check if file is open
        if (!fileTable.isDefined(fileName)) {
            throw new StatementException("File '" + fileName.getVal() + "' is not open");
        }

        BufferedReader br = fileTable.lookup(fileName);
        try {
            br.close();
            fileTable.remove(fileName);
        } catch (IOException e) {
            throw new StatementException("Error closing file '" + fileName.getVal() + "': " + e.getMessage());
        }

        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typeExp = exp.typecheck(typeEnv);
        if (typeExp.equals(new StringType())) {
            return typeEnv;
        } else {
            throw new MyException("CloseRFile: The file path expression must be a StringType.");
        }
    }

    @Override
    public String toString() {
        return "closeRFile(" + exp.toString() + ")";
    }
}