package Java.tools;

import Java.Main;
import Java.Strings;
import Java.models.SceneHistory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

public class WindowsManager {

    private static ArrayList<SceneHistory> sceneHistories = new ArrayList<>();
    private static String windowParams = "";
    private static WindowsManager ourInstance = new WindowsManager();

    public static WindowsManager getInstance() {
        return ourInstance;
    }

    private WindowsManager() {
    }

    public static int getCurrentScreenPosition() {
        return sceneHistories.size() - 1;
    }

    public static String getWindowParams() {
        return windowParams;
    }

    public static SceneHistory getRootScene(){
        return sceneHistories.get(0);
    }

    public static void addSceneToHistory(Scene scene, String title) {
        sceneHistories.add(new SceneHistory(scene, title));
    }

    public static SceneHistory getCurrentSceneFromHistory() {
        return sceneHistories.get(getCurrentScreenPosition());
    }

    public static void goToPreviousScene(Stage stage) {
        sceneHistories.remove(getCurrentScreenPosition());
        Scene scene = sceneHistories.get(getCurrentScreenPosition()).getScene();
        stage.setWidth(stage.getWidth());
        stage.setHeight(stage.getHeight());
        stage.setScene(scene);
        stage.setTitle(sceneHistories.get(getCurrentScreenPosition()).getTitle());
        stage.setFullScreen(Main.isFullScreen);
    }

    public static void goToNewScene(Stage stage, String layout) {
        goToNewScene(stage, layout, "");
    }

    public static void goToNewScene(Stage stage, String layout, String param) {
        try {
            windowParams = param;
            Parent showPageRoot = FXMLLoader.load(Main.class.getResource(ProjectPaths.JAR_LAYOUTS_FOLDER_PATH + layout));
            Scene scene = new Scene(showPageRoot, stage.getWidth(), stage.getHeight());

            if (param.equals(Strings.APP_NAME))
                stage.setTitle(Strings.APP_NAME);
            else
                stage.setTitle(Strings.APP_NAME + " ~ " + windowParams);

            stage.setScene(scene);
            stage.setFullScreen(Main.isFullScreen);
            sceneHistories.add(new SceneHistory(scene, stage.getTitle()));
        } catch (Exception e) {
            System.out.println("WindowsManager " + e.toString());
        }
    }

    public static void refreshScene(Stage stage, String layout) {
        refreshScene(stage, layout, sceneHistories.get(getCurrentScreenPosition()).getTitle());
    }

    public static void refreshScene(Stage stage, String layout, String title) {

        try {
            Parent showPageRoot = FXMLLoader.load(Main.class.getResource(ProjectPaths.JAR_LAYOUTS_FOLDER_PATH + layout));
            Scene scene = new Scene(showPageRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            stage.setFullScreen(Main.isFullScreen);
            stage.setTitle(title);
            sceneHistories.remove(getCurrentScreenPosition());
            sceneHistories.add(new SceneHistory(scene, title));
        } catch (IOException e) {
            e.getCause().printStackTrace();
        }

    }
}