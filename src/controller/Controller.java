package controller;

import exception.MyException;
import model.state.PrgState;
import model.value.RefValue;
import model.value.Value;
import repository.IRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller {
    private IRepository repo;
    private ExecutorService executor;

    public Controller(IRepository repo) {
        this.repo = repo;
        this.executor = Executors.newFixedThreadPool(2);
    }

    public IRepository getRepository() {
        return repo;
    }

    public List<PrgState> removeCompletedPrg(List<PrgState> inPrgList) {
        return inPrgList.stream()
                .filter(PrgState::isNotCompleted)
                .collect(Collectors.toList());
    }

    private Map<Integer, Value> safeGarbageCollector(List<PrgState> prgList, Map<Integer, Value> heap) {
        // Get all addresses from ALL symbol tables
        List<Integer> symTableAddr = prgList.stream()
                .flatMap(p -> p.getSymTable().getContent().values().stream())
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).getAddr())
                .collect(Collectors.toList());

        // Get all addresses from HEAP (indirect references)
        List<Integer> heapAddr = heap.values().stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).getAddr())
                .collect(Collectors.toList());

        // Filter the heap
        return heap.entrySet().stream()
                .filter(e -> symTableAddr.contains(e.getKey()) || heapAddr.contains(e.getKey()))
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void oneStepForAllPrg(List<PrgState> prgList) throws InterruptedException {
        // Log before execution
        prgList.forEach(prg -> {
            try {
                repo.logPrgStateExec(prg);
            } catch (MyException e) {
                System.out.println("Log Error: " + e.getMessage());
            }
        });

        // Prepare Callables
        List<Callable<PrgState>> callList = prgList.stream()
                .map((PrgState p) -> (Callable<PrgState>) (() -> {
                    if (p.isNotCompleted()) {
                        return p.oneStep();
                    }
                    return null;
                }))
                .collect(Collectors.toList());

        // Execute Callables
        List<PrgState> newPrgList = executor.invokeAll(callList).stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.out.println("Execution Error: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Add new threads to the list
        prgList.addAll(newPrgList);

        // Log after execution
        prgList.forEach(prg -> {
            try {
                repo.logPrgStateExec(prg);
            } catch (MyException e) {
                System.out.println("Log Error: " + e.getMessage());
            }
        });

        // Update repository
        repo.setPrgList(prgList);
    }

    public void allStep() throws MyException, InterruptedException {
        executor = Executors.newFixedThreadPool(2);

        // Initial removal of completed programs
        List<PrgState> prgList = removeCompletedPrg(repo.getPrgList());

        while (prgList.size() > 0) {
            // Garbage Collector
            prgList.get(0).getHeap().setContent(
                    safeGarbageCollector(prgList, prgList.get(0).getHeap().getContent())
            );

            // Execute one step
            oneStepForAllPrg(prgList);

            // Remove completed programs
            prgList = removeCompletedPrg(repo.getPrgList());
        }

        executor.shutdownNow();

        // Final repo update
        repo.setPrgList(prgList);
    }
}