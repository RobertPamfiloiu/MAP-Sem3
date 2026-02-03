package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIDictionary;
import model.expression.Exp;
import model.state.PrgState;
import model.type.StringType;
import model.value.StringValue;
import model.value.Value;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import model.type.Type;


public class OpenRFile implements IStmt {
    private final Exp exp;

    public OpenRFile(Exp exp) {
        this.exp = exp;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        // Evaluate the expression
        Value val = exp.eval(state.getSymTable(), state.getHeap());
        // Check if it's a StringType
        if (!val.getType().equals(new StringType())) {
            throw new StatementException("Expression did not evaluate to a string");
        }

        StringValue fileName = (StringValue) val;
        MyIDictionary<StringValue, BufferedReader> fileTable = state.getFileTable();

        // Check if file is already open
        if (fileTable.isDefined(fileName)) {
            throw new StatementException("File '" + fileName.getVal() + "' is already open");
        }

        // Open the file
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName.getVal()));
            fileTable.put(fileName, br); // Add to FileTable
        } catch (IOException e) {
            throw new StatementException("Could not open file '" + fileName.getVal() + "': " + e.getMessage());
        }

        return null;
    }

    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        Type typeExp = exp.typecheck(typeEnv);
        if (typeExp.equals(new StringType())) {
            return typeEnv;
        } else {
            throw new MyException("OpenRFile: The file path expression must be a StringType.");
        }
    }

    @Override
    public String toString() {
        return "openRFile(" + exp.toString() + ")";
    }
}