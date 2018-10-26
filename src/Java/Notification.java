package Java;

import Java.tools.ProjectPaths;
import Java.tools.WindowsManager;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class Notification {

    private Alert alert = new Alert(Alert.AlertType.INFORMATION);


    public Notification(String title, String headerText, String text) {

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(text);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getButtonTypes().remove(0);
        alert.setResult(ButtonType.OK);
        alert.getDialogPane().setMinHeight(160);
        alert.getDialogPane().setMinWidth(480);
        alert.setWidth(480);
        alert.setHeight(160);
    }


    private void setPosition() {
        if(WindowsManager.getCurrentScreenPosition() >= 0) {
            Stage stage = (Stage) WindowsManager.getCurrentSceneFromHistory().getScene().getWindow();
            alert.setX(stage.getX() + (stage.getWidth() / 2) - alert.getWidth() / 2);
            alert.setY(stage.getY() + (stage.getHeight() / 2));
        }
    }

    public void run(){
        //Task doesn't work on MACs.
        if(ProjectPaths.getInstance().getOS() == ProjectPaths.OS.MAC) {
            onRun();
            onComplete();
            return;
        }

        setPosition();

        Task<Boolean> loadingTask = new Task<Boolean>() {
            @Override
            protected Boolean call() {
                onRun();
                return null;
            }
        };

        loadingTask.setOnRunning(workerStateEvent -> alert.show());

        loadingTask.setOnSucceeded(workerStateEvent -> {
            alert.hide();
            onComplete();
        });

        new Thread(loadingTask).start();

    }


    public void setTitle(String title){
        alert.setTitle(title);
    }

    public void setText(String text){
        alert.setContentText(text);
    }

    public void setHeaderText(String text){
        alert.setHeaderText(text);
    }

    public abstract void onRun();

    public abstract void onComplete();



}
