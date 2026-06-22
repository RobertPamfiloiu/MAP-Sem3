# MAP-Sem3: Toy Language Interpreter

A multithreaded interpreter for a small imperative "Toy Language", built for the **Metode Avansate de Programare (MAP)** course at Babeș-Bolyai University. The interpreter parses programs defined as Java object trees, type-checks them, and executes them step by step, with support for concurrency (`fork`), heap-allocated references, file I/O, and automatic garbage collection. It ships with a JavaFX graphical interface for selecting and running example programs.

## Features

- **Step-by-step execution engine**: programs run one statement at a time, so intermediate state (execution stack, symbol table, heap, output) can be inspected after every step.
- **Concurrency via `fork`**: a `ForkStmt` spawns a new program state that shares the heap and file table with its parent but gets its own execution stack and symbol table. All program states are advanced concurrently using an `ExecutorService` with a fixed thread pool of 2.
- **Heap and references**: `new`, `writeHeap`, and `readHeap` (`rH`) operations over a reference-counted heap, including references to references. The heap uses a `ConcurrentHashMap` and an `AtomicInteger` for thread-safe allocation.
- **Garbage collector**: a conservative GC runs before each execution step, keeping only heap addresses reachable from any program's symbol table or from other live heap entries.
- **Static type checking**: every program is type-checked before execution begins; programs that fail the check are reported and excluded from the run.
- **File I/O**: `openRFile`, `readFile`, and `closeRFile` statements with a per-state file table.
- **JavaFX GUI**: a program chooser plus an executor view that displays the current program state. Each run is logged to a text file.

## Toy Language Overview

**Types:** `int`, `bool`, `string`, and reference types `Ref(Type)`.

**Values:** integer, boolean, string, and reference values.

**Expressions:** arithmetic (`ArithExp`), logical (`LogicExp`), relational (`RelationalExp`), variable (`VarExp`), constant (`ValueExp`), and heap read (`ReadHeapExp`).

**Statements:**

| Statement | Purpose |
| --- | --- |
| `VarDeclStmt` | declare a variable |
| `AssignStmt` | assign a value to a variable |
| `CompStmt` | sequence two statements |
| `IfStmt` | conditional branching |
| `WhileStmt` | loop while a condition holds |
| `PrintStmt` | append a value to the output list |
| `ForkStmt` | spawn a new concurrent program state |
| `NewStmt` | allocate a value on the heap |
| `WriteHeapStmt` | write to a heap address |
| `ReadHeapExp` | read from a heap address (expression) |
| `OpenRFile` / `ReadFile` / `CloseRFile` | file input operations |
| `NopStmt` | no operation |

## Project Structure

The code follows a layered architecture:

```
src/
├── controller/      # Controller: orchestrates execution, runs the GC and thread pool
├── repository/      # IRepository / Repository: holds program states, logs to file
├── model/
│   ├── state/       # PrgState: exec stack, symbol table, output, file table, heap
│   ├── statement/   # IStmt and all statement implementations
│   ├── expression/  # Exp and all expression implementations
│   ├── value/       # Value types (Int, Bool, String, Ref)
│   ├── type/        # Type definitions (Int, Bool, String, Ref)
│   └── adt/         # MyStack, MyList, MyDictionary, MyHeap (+ interfaces)
├── exception/       # MyException and specific exception subclasses
└── view/            # JavaFX entry point, FXML controllers, and a legacy text menu
resources/           # FXML layouts (ProgramListLayout, ProgramExecutorLayout)
```

A program state (`PrgState`) bundles five components: an **execution stack** of statements, a **symbol table** mapping names to values, an **output list**, a **file table**, and a shared **heap**. Each state has a thread-safe unique ID.

## Requirements

- **JDK 25**
- **JavaFX SDK 25** (the project is configured against `javafx-sdk-25.0.1`)
- IntelliJ IDEA (the repo includes the `.idea` project files and a `Homework_5.iml` module)

## Building and Running

The project is set up as an IntelliJ module. To run it:

1. Open the project in IntelliJ IDEA.
2. Add the JavaFX SDK as a project library (update the path under **Project Structure → Libraries** to point at your local `javafx-sdk-25.x/lib`, since the checked-in path is machine-specific).
3. Add the JavaFX modules to the run configuration VM options:

   ```
   --module-path /path/to/javafx-sdk-25/lib --add-modules javafx.controls,javafx.fxml
   ```

4. Run `view.Interpreter` (the JavaFX `Application` entry point).

A program-chooser window opens listing the bundled examples. Select one to open the executor view and run it.

## Bundled Example Programs

1. **Heap Allocation & Reading**: allocates values on the heap and reads through reference-to-reference chains.
2. **While Loop**: counts down from 4 to 0, printing each value.
3. **Garbage Collector Test**: reassigns a reference so that an earlier heap value becomes unreachable, demonstrating collection.
4. **Fork & Concurrency**: forks a child that mutates a shared heap cell while the parent observes, showing concurrent execution and shared-heap semantics.

The `.txt` files in the repository root (`1HeapAllocationReading.txt`, `2WhileLoop.txt`, `4ForkConcurrency.txt`) and the `logN.txt` files are execution log outputs from these runs.
