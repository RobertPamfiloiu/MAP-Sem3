package model.state;

import exception.MyException;
import model.adt.MyIDictionary;
import model.adt.MyIHeap;
import model.adt.MyIList;
import model.adt.MyIStack;
import model.statement.IStmt;
import model.value.StringValue;
import model.value.Value;

import java.io.BufferedReader;

public class PrgState {
    private MyIStack<IStmt> exeStack;
    private MyIDictionary<String, Value> symTable;
    private MyIList<Value> out;
    private MyIDictionary<StringValue, BufferedReader> fileTable;
    private MyIHeap<Value> heap;
    private int id; // Unique ID for the thread
    private static int lastId = 0; // Static counter

    // Method to manage thread IDs safely
    public static synchronized int getNewId() {
        return ++lastId;
    }

    public PrgState(MyIStack<IStmt> stk, MyIDictionary<String, Value> symtbl,
                    MyIList<Value> ot, MyIDictionary<StringValue, BufferedReader> ft,
                    MyIHeap<Value> heap, IStmt prg) {
        this.exeStack = stk;
        this.symTable = symtbl;
        this.out = ot;
        this.fileTable = ft;
        this.heap = heap;
        this.id = getNewId();
        if (prg != null) {
            stk.push(prg);
        }
    }

    public MyIStack<IStmt> getStk() { return exeStack; }
    public MyIDictionary<String, Value> getSymTable() { return symTable; }
    public MyIList<Value> getOut() { return out; }
    public MyIDictionary<StringValue, BufferedReader> getFileTable() { return fileTable; }
    public MyIHeap<Value> getHeap() { return heap; }
    public int getId() { return id; }

    public void setHeap(MyIHeap<Value> heap) { this.heap = heap; }

    // Check if execution is not completed
    public boolean isNotCompleted() {
        return !exeStack.isEmpty();
    }

    // oneStep logic moved from Controller
    public PrgState oneStep() throws MyException {
        if (exeStack.isEmpty()) throw new MyException("PrgState stack is empty");
        IStmt crtStmt = exeStack.pop();
        return crtStmt.execute(this);
    }

    @Override
    public String toString() {
        return "Id: " + id + "\nExeStack:\n" + exeStack.toString() +
                "\nSymTable:\n" + symTable.toString() +
                "\nOut:\n" + out.toString() +
                "\nFileTable:\n" + fileTable.getContent().keySet().toString() +
                "\nHeap:\n" + heap.toString() + "\n---------------------------------\n";
    }
}