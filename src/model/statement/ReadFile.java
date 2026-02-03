package model.statement;

import exception.MyException;
import exception.StatementException;
import model.adt.MyIDictionary;
import model.expression.Exp;
import model.state.PrgState;
import model.type.IntType;
import model.type.StringType;
import model.value.IntValue;
import model.value.StringValue;
import model.value.Value;
import model.type.Type;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadFile implements IStmt {
    private final Exp exp;
    private final String varName;

    public ReadFile(Exp exp, String varName) {
        this.exp = exp;
        this.varName = varName;
    }

    @Override
    public PrgState execute(PrgState state) throws MyException {
        MyIDictionary<String, Value> symTable = state.getSymTable();
        // Check if var_name is defined in SymTable and is int
        if (!symTable.isDefined(varName)) {
            throw new StatementException("Variable '" + varName + "' is not defined");
        }
        if (!symTable.lookup(varName).getType().equals(new IntType())) {
            throw new StatementException("Variable '" + varName + "' is not of type int");
        }

        // Evaluate expression to a string value
        Value val = exp.eval(symTable, state.getHeap());
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
            String line = br.readLine();
            int intValue;
            if (line == null || line.trim().isEmpty()) {
                intValue = 0; // EOF or empty line, assign 0
            } else {
                try {
                    intValue = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    throw new StatementException("File line '" + line + "' is not a valid integer");
                }
            }
            // Update SymTable
            symTable.update(varName, new IntValue(intValue));
        } catch (IOException e) {
            throw new StatementException("Error reading file '" + fileName.getVal() + "': " + e.getMessage());
        }

        return null;
    }
    @Override
    public MyIDictionary<String, Type> typecheck(MyIDictionary<String, Type> typeEnv) throws MyException {
        // 1. Check if the expression (filename) evaluates to String
        Type typeExp = exp.typecheck(typeEnv);
        if (!typeExp.equals(new StringType())) {
            throw new MyException("ReadFile: The file path expression must be a StringType.");
        }

        // 2. Check if the variable (varName) is defined and is IntType
        Type typeVar = typeEnv.lookup(varName);
        if (!typeVar.equals(new IntType())) {
            throw new MyException("ReadFile: The variable '" + varName + "' must be an IntType.");
        }

        return typeEnv;
    }

    @Override
    public String toString() {
        return "readFile(" + exp.toString() + ", " + varName + ")";
    }
}