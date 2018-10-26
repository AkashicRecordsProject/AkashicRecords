package Java.models;

import javafx.scene.Scene;

public class SceneHistory {

    private String title;
    private Scene scene;

    public SceneHistory(Scene scene, String title) {
        this.title = title;
        this.scene = scene;
    }


    public String getTitle() {
        return title;
    }

    public Scene getScene() {
        return scene;
    }
}
