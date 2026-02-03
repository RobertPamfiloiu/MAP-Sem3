package view;

import controller.Controller;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.adt.MyIHeap;
import model.state.PrgState;
import model.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ProgramExecutorController {
    private Controller controller;

    @FXML private TextField numberOfPrgStatesTextField;
    @FXML private TableView<Map.Entry<Integer, Value>> heapTableView;
    @FXML private TableColumn<Map.Entry<Integer, Value>, Integer> addressColumn;
    @FXML private TableColumn<Map.Entry<Integer, Value>, String> valueColumn;
    @FXML private ListView<String> outputListView;
    @FXML private ListView<String> fileTableListView;
    @FXML private ListView<Integer> prgStateIdentifiersListView;
    @FXML private TableView<Map.Entry<String, Value>> symbolTableView;
    @FXML private TableColumn<Map.Entry<String, Value>, String> symVarNameColumn;
    @FXML private TableColumn<Map.Entry<String, Value>, String> symValueColumn;
    @FXML private ListView<String> executionStackListView;
    @FXML private Button runOneStepButton;
    @FXML private Button runAllButton; // Make sure this exists in your FXML!

    public void setController(Controller controller) {
        this.controller = controller;
        populate();
    }

    @FXML
    public void initialize() {
        prgStateIdentifiersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        addressColumn.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().getKey()).asObject());
        valueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue().toString()));
        symVarNameColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));
        symValueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue().toString()));

        runOneStepButton.setOnAction(actionEvent -> runOneStep());
        if(runAllButton != null) {
            runAllButton.setOnAction(actionEvent -> runAllSteps());
        }

        prgStateIdentifiersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeProgramState());
    }

    private void runOneStep() {
        if (controller == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No program selected", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            List<PrgState> prgList = controller.removeCompletedPrg(controller.getRepository().getPrgList());

            if (prgList.size() > 0) {
                controller.oneStepForAllPrg(prgList);
                populate();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Program finished", ButtonType.OK);
                alert.showAndWait();

                populate(); // 1. Update the GUI first (while data still exists!)
                controller.getRepository().setPrgList(prgList); // 2. THEN clear the memory
            }
        } catch (InterruptedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void runAllSteps() {
        if (controller == null) return;

        try {
            // Loop until list is empty
            while(controller.getRepository().getPrgList().size() > 0) {
                List<PrgState> prgList = controller.removeCompletedPrg(controller.getRepository().getPrgList());
                if(prgList.isEmpty()) break;

                controller.oneStepForAllPrg(prgList);
            }
            populate(); // Update UI at the very end

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Run All Finished", ButtonType.OK);
            alert.showAndWait();

        } catch (InterruptedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void populate() {
        // 1. Get the list of currently running programs
        List<PrgState> prgList = controller.getRepository().getPrgList();
        numberOfPrgStatesTextField.setText(String.valueOf(prgList.size()));

        // 2. IF THE PROGRAM IS FINISHED (List is empty)
        if (prgList.isEmpty()) {
            // Clear only the "execution" tables
            heapTableView.getItems().clear();
            prgStateIdentifiersListView.getItems().clear();
            symbolTableView.getItems().clear();
            executionStackListView.getItems().clear();

            // CRITICAL FIX: We do NOT clear the Output List or File Table here.
            // This ensures the final result (4 3 2 1 0) stays visible on the screen.
            return;
        }

        // 3. IF THE PROGRAM IS RUNNING
        // Get the first available program (usually the main thread) to display shared data
        PrgState firstProgram = prgList.get(0);

        // --- HEAP TABLE ---
        // Using setItems automatically replaces the old list (fixes duplicates)
        MyIHeap<Value> heap = firstProgram.getHeap();
        List<Map.Entry<Integer, Value>> heapList = new ArrayList<>(heap.getContent().entrySet());
        heapTableView.setItems(FXCollections.observableArrayList(heapList));
        heapTableView.refresh();

        // --- OUTPUT LIST ---
        // Using setItems automatically replaces the old list (fixes the 4 4 3 2 1 bug)
        outputListView.setItems(FXCollections.observableArrayList(
                firstProgram.getOut().getContent().stream()
                        .map(Value::toString)
                        .collect(Collectors.toList())));

        // --- FILE TABLE ---
        fileTableListView.setItems(FXCollections.observableArrayList(
                firstProgram.getFileTable().getContent().keySet().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())));

        // --- PROGRAM ID LIST ---
        List<Integer> idList = prgList.stream()
                .map(PrgState::getId)
                .collect(Collectors.toList());
        prgStateIdentifiersListView.setItems(FXCollections.observableArrayList(idList));

        // --- UPDATE SYMBOL TABLE & STACK ---
        changeProgramState();
    }

    @FXML
    private void changeProgramState() {
        Integer selectedId = prgStateIdentifiersListView.getSelectionModel().getSelectedItem();
        if (selectedId == null) {
            if (!controller.getRepository().getPrgList().isEmpty()) {
                selectedId = controller.getRepository().getPrgList().get(0).getId();
                prgStateIdentifiersListView.getSelectionModel().select(selectedId);
            } else {
                return;
            }
        }

        Integer finalSelectedId = selectedId;
        PrgState selectedPrg = controller.getRepository().getPrgList().stream()
                .filter(p -> p.getId() == finalSelectedId)
                .findFirst()
                .orElse(null);

        if (selectedPrg != null) {
            List<Map.Entry<String, Value>> symList = new ArrayList<>(selectedPrg.getSymTable().getContent().entrySet());
            symbolTableView.setItems(FXCollections.observableArrayList(symList));
            symbolTableView.refresh();

            List<String> stackList = selectedPrg.getStk().getReversedStack().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            executionStackListView.setItems(FXCollections.observableArrayList(stackList));
        }
    }
}