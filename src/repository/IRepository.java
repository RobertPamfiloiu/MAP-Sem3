package repository;

import exception.MyException;
import model.state.PrgState;
import java.util.List;

public interface IRepository {
    // Manage a list of programs
    List<PrgState> getPrgList();
    void setPrgList(List<PrgState> prgList);

    // Log specific program state
    void logPrgStateExec(PrgState prgState) throws MyException;
}