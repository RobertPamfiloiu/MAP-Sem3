package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class ProgramChooserController {

    @FXML
    private ListView<Interpreter.ProgramOption> programsListView;

    @FXML
    private Button displayButton;

    public void setProgramList(List<Interpreter.ProgramOption> programs) {
        programsListView.setItems(FXCollections.observableArrayList(programs));
    }

    @FXML
    public void initialize() {
        displayButton.setOnAction(actionEvent -> {
            Interpreter.ProgramOption selectedOption = programsListView.getSelectionModel().getSelectedItem();
            if (selectedOption == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("No program selected!");
                alert.showAndWait();
            } else {
                runProgram(selectedOption.getController());
            }
        });
    }

    private void runProgram(Controller controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProgramExecutorLayout.fxml"));
            Parent root = loader.load();

            ProgramExecutorController executorController = loader.getController();
            executorController.setController(controller);

            Stage stage = new Stage();
            stage.setTitle("Program Execution");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}