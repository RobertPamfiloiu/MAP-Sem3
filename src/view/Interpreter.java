package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.Controller;
import model.adt.MyDictionary;
import model.adt.MyHeap;
import model.adt.MyList;
import model.adt.MyStack;
import model.expression.*;
import model.state.PrgState;
import model.statement.*;
import model.type.*;
import model.value.*;
import repository.IRepository;
import repository.Repository;
import exception.MyException;

import java.util.ArrayList;
import java.util.List;

public class Interpreter extends Application {

    // Helper class to display name in ListView
    public static class ProgramOption {
        private String name;
        private Controller controller;

        public ProgramOption(String name, Controller controller) {
            this.name = name;
            this.controller = controller;
        }

        public Controller getController() {
            return controller;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProgramListLayout.fxml"));
        Parent root = loader.load();

        ProgramChooserController listController = loader.getController();

        List<ProgramOption> myPrograms = new ArrayList<>();

        // --- Example 1 ---
        IStmt ex1 = new CompStmt(new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(new NewStmt("a", new VarExp("v")),
                                        new CompStmt(new PrintStmt(new ReadHeapExp(new VarExp("v"))),
                                                new PrintStmt(new ArithExp('+',
                                                        new ReadHeapExp(new ReadHeapExp(new VarExp("a"))),
                                                        new ValueExp(new IntValue(5)))))))));
        addProgram(myPrograms, ex1, "1. Heap Allocation & Reading");

        // --- Example 2 ---
        IStmt ex2 = new CompStmt(new VarDeclStmt("v", new IntType()),
                new CompStmt(new AssignStmt("v", new ValueExp(new IntValue(4))),
                        new CompStmt(new WhileStmt(
                                new RelationalExp(">", new VarExp("v"), new ValueExp(new IntValue(0))),
                                new CompStmt(new PrintStmt(new VarExp("v")),
                                        new AssignStmt("v", new ArithExp('-', new VarExp("v"), new ValueExp(new IntValue(1)))))),
                                new PrintStmt(new VarExp("v")))));
        addProgram(myPrograms, ex2, "2. While Loop");

        // --- Example 3 ---
        IStmt ex3 = new CompStmt(new VarDeclStmt("v", new RefType(new IntType())),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(20))),
                        new CompStmt(new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                                new CompStmt(new NewStmt("a", new VarExp("v")),
                                        new CompStmt(new NewStmt("v", new ValueExp(new IntValue(30))),
                                                new PrintStmt(new ReadHeapExp(new ReadHeapExp(new VarExp("a")))))))));
        addProgram(myPrograms, ex3, "3. Garbage Collector Test");

        // --- Example 6 (Fork) ---
        IStmt ex6 = new CompStmt(new VarDeclStmt("v", new IntType()),
                new CompStmt(new VarDeclStmt("a", new RefType(new IntType())),
                        new CompStmt(new AssignStmt("v", new ValueExp(new IntValue(10))),
                                new CompStmt(new NewStmt("a", new ValueExp(new IntValue(22))),
                                        new CompStmt(new ForkStmt(new CompStmt(new WriteHeapStmt("a", new ValueExp(new IntValue(30))),
                                                new CompStmt(new AssignStmt("v", new ValueExp(new IntValue(32))),
                                                        new CompStmt(new PrintStmt(new VarExp("v")),
                                                                new PrintStmt(new ReadHeapExp(new VarExp("a"))))))),
                                                new CompStmt(new PrintStmt(new VarExp("v")),
                                                        new PrintStmt(new ReadHeapExp(new VarExp("a")))))))));
        addProgram(myPrograms, ex6, "4. Fork & Concurrency");

        // Pass list to controller
        listController.setProgramList(myPrograms);

        primaryStage.setTitle("Select Program");
        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();
    }

    private void addProgram(List<ProgramOption> list, IStmt stmt, String name) {
        try {
            stmt.typecheck(new MyDictionary<>());
            PrgState prgState = new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(), new MyDictionary<>(), new MyHeap<>(), stmt);
            String safeName = name.replaceAll("[^a-zA-Z0-9]", "") + ".txt";
            IRepository repo = new Repository(prgState, safeName);
            Controller controller = new Controller(repo);
            list.add(new ProgramOption(name, controller));
        } catch (MyException e) {
            System.out.println("TypeCheck failed for " + name + ": " + e.getMessage());
        }
    }
}