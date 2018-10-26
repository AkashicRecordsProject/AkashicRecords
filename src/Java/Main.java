package Java;

import Java.models.AiringShowList;
import Java.models.AkashicRecordsSettings;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import Java.tools.WindowsManager;
import Java.web.WebSearch;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.awt.*;
import java.io.File;


public class Main extends Application {


    public static boolean isFullScreen = false;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {


        ProjectPaths.getInstance().createPaths();
        User.getInstance().create();
        AkashicRecordsSettings.getInstance().create();

        //if the user is connecting through a shared file recreate the paths
        if (AkashicRecordsSettings.getInstance().isUseNetwork()) {

            ProjectPaths.getInstance().createPaths();

            if (AkashicRecordsSettings.getInstance().isUseNetworkUser())
                User.getInstance().create();
        }

        AkashicRecordsSettings.getInstance().create();


        if (!new File(ProjectPaths.getInstance().getCoverImagesFolder() + "empty.jpg").exists()) {
            Utils.getInstance().createImageFromResources(ProjectPaths.getInstance().getCoverImagesFolder(),
                    "empty.jpg", ProjectPaths.JAR_IMAGES_FOLDER_PATH, "empty.jpg", "jpg");
        }

        if (!new File(ProjectPaths.getInstance().getBackgroundImagesFolder() + "empty.jpg").exists()) {
            Utils.getInstance().createImageFromResources(ProjectPaths.getInstance().getBackgroundImagesFolder(),
                    "empty.jpg", ProjectPaths.JAR_IMAGES_FOLDER_PATH, "empty_background.jpg", "jpg");
        }


        for (User.THEME theme : User.THEME.values()) {
            if(!new File(ProjectPaths.getInstance().getHeaderFolder() + theme.getTitle() + ".jpg").exists()) {
                Utils.getInstance().createImageFromResources(ProjectPaths.getInstance().getHeaderFolder(),
                        theme.getTitle() + ".jpg", ProjectPaths.JAR_IMAGES_FOLDER_PATH,
                        theme.getTitle() + ".jpg", "jpg");
            }
        }


        loadFont("Roboto-Light.ttf", 10);
        loadFont("Roboto-Bold.ttf", 10);
        loadFont("MSMINCHO.ttf", 10);
        loadFont("NotoSans-Regular.ttf", 100);


        if (new File(ProjectPaths.getInstance().getJsonOtherFolder(), Strings.JSON_AIRING_SHOWS).exists()) {

            //update airing list if a new season has started
            if (!Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonOtherFolder(),
                    Strings.JSON_AIRING_SHOWS, AiringShowList.class)
                    .getDate().equals(Utils.getCurrentSeason(true))) {

                new WebSearch().createJsonForAiringShowsMal();
            }

        } else { //create airing list if doesn't exist
            new WebSearch().createJsonForAiringShowsMal();
        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource(ProjectPaths.JAR_LAYOUTS_FOLDER_PATH + Strings.LAYOUT_SHOW_CATALOG));
        Parent root = loader.load();
        primaryStage.setTitle(Strings.APP_NAME);
        WindowsManager.addSceneToHistory(new Scene(root), primaryStage.getTitle());
        primaryStage.setScene(WindowsManager.getCurrentSceneFromHistory().getScene());

        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
        Rectangle bounds = graphicsDevices[0].getConfigurations()[0].getBounds();

        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.show();

        primaryStage.getIcons().add(new Image(ProjectPaths.JAR_ICONS_FOLDER_PATH + Strings.ICON_AKASHIC_RECORDS));

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {

            if (KeyboardShortcuts.FULLSCREEN.match(keyEvent)) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                isFullScreen = primaryStage.isFullScreen();
            } else if (KeyboardShortcuts.BACK.match(keyEvent)) {
                if (WindowsManager.getCurrentScreenPosition() > 0) {
                    WindowsManager.goToPreviousScene((Stage) primaryStage.getScene().getWindow());
                }
            }

        });

    }


    private void loadFont(String fontName, int fontSize) {
        Font.loadFont(getClass().getResourceAsStream(ProjectPaths.JAR_FONTS_FOLDER_PATH + fontName), fontSize);
    }


}