package Java.controllers;


import Java.*;
import Java.comparators.DateComparator;
import Java.comparators.RatingComparator;
import Java.comparators.AlphabeticalComparator;
import Java.models.*;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import Java.tools.WindowsManager;
import Java.web.ShowInfoCreator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CatalogPageController implements Initializable {

    public enum SORT {
        ALPHABETIC,
        DATE,
        RATING
    }

    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private AnchorPane root, headerImage, mainAiringContainer, mainWatchingContainer;
    @FXML
    private FlowPane allContainer, watchingContainer, airingContainer, genreTagFlowContainer, studioTagFlowContainer;
    @FXML
    private ImageView menuAll, menuGenreTag, menuStudioTag, menuUpdate, menuBack;
    @FXML
    private Label allTitleLabel, watchingTitleLabel, airingTitleLabel;
    @FXML
    private VBox showContainerRoot;
    @FXML
    private Button navigationArrow;
    @FXML
    private TextField menuSearch;

    private static final HashMap<String, Image> thumbnailMap = new HashMap<>();
    private static final HashMap<ImageView, ShowInfo> missingThumbnailMap = new HashMap<>();

    private String titleModifier = "";

    private TagContainer tagContainer = new TagContainer();
    private ArrayList<String> genreTags = new ArrayList<>();
    private ArrayList<String> studioTags = new ArrayList<>();
    private ArrayList<String> airingShows = new ArrayList<>();

    private boolean isScrollingUp = true;
    private boolean isCtrlHeld = false;

    private SORT currentSort = SORT.ALPHABETIC;
    private static Notification notification;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User user = User.getInstance();
        ArrayList<ShowInfo> shows = null;

        try {
            shows = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonContainersFolder(),
                    Strings.JSON_ALL_SHOWS, ShowContainer.class).getShows();
        } catch (Exception ignored) {
        }

        if (shows == null) {
            shows = new ArrayList<>();
            Utils.saveJsonToFile(new ShowContainer(), ProjectPaths.getInstance().getJsonContainersFolder(),
                    Strings.JSON_ALL_SHOWS);
        }


        AiringShowList airingShowList = Utils.getInstance().createObjectFromJson(
                ProjectPaths.getInstance().getJsonOtherFolder(), Strings.JSON_AIRING_SHOWS, AiringShowList.class);

        try {
            for (ShowInfo show : shows) {
                for (String airingShow : airingShowList.getShowList()) {

                    String showTitle = Utils.getDefaultShowTitle(show.getMalTitle()).replace("(tv)", "");
                    String airingShowTitle = Utils.getDefaultShowTitle(airingShow).replace("(tv)", "");

                    if (airingShowTitle.contains(showTitle) &&
                            airingShowTitle.split("_")[0].equals(showTitle.split("_")[0])) {

                        if (user.isShowOnlyWatchingInAiring() && user.isWatchingShow(show.getTitle()))
                            airingShows.add(show.getTitle());
                        else if (!user.isShowOnlyWatchingInAiring())
                            airingShows.add(show.getTitle());

                        break;
                    }
                }
            }
        } catch (Exception ignored) {

        }

        root.getStylesheets().add(ProjectPaths.JAR_CSS_FOLDER_PATH + user.getThemeName(true));


        headerImage.setStyle("-fx-background-image: url('file:////" + ProjectPaths.getInstance().getHeaderFolder().replace("file://", "") + user.getThemeName(false) + ".jpg" + "');");

        allContainer.heightProperty().addListener((observable, oldValue, newValue) -> updateParallax());
        allTitleLabel.prefWidthProperty().bind(allContainer.widthProperty());
        watchingTitleLabel.prefWidthProperty().bind(watchingContainer.widthProperty());
        airingTitleLabel.prefWidthProperty().bind(airingContainer.widthProperty());


        allTitleLabel.setText(Strings.TITLE_ALL_SHOWS);
        watchingTitleLabel.setText(Strings.TITLE_WATCHING);
        airingTitleLabel.setText(Strings.TITLE_AIRING_SHOWS);


        //creating the show covers and labels
        for (ShowInfo show : shows) {

            //used to speed up testing
            if (Utils.runningFromIntelliJ() && show.getTitle().startsWith("B"))
                break;

            StringBuilder tags = new StringBuilder(show.getTitle() + Strings.TAG_SEPARATOR + show.getStudio() + Strings.TAG_SEPARATOR);

            if (!studioTags.contains(show.getStudio()))
                studioTags.add(show.getStudio());

            for (String tag : show.getVisibleTags()) {
                tags.append(tag).append(Strings.TAG_SEPARATOR);

                if (!genreTags.contains(tag))
                    genreTags.add(tag);
            }

            if (user.isShowAiring() && airingShows.contains(show.getTitle())) {
                airingContainer.getChildren().add(createShowContainer(show));
            } else if (user.isWatchingShow(show.getTitle())) {
                watchingContainer.getChildren().add(createShowContainer(show));
            }

            VBox showCoverContainer = createShowContainer(show);
            tagContainer.getTags().add(new TagObject(tags.toString(), showCoverContainer));
            allContainer.getChildren().add(showCoverContainer);

        }

        notification = new Notification(Strings.DIALOG_THUMBNAIL_CREATION_TITLE, Strings.DIALOG_THUMBNAIL_CREATION_HEADER, "") {
            @Override
            public void onRun() {
                updateThumbnails();
            }

            @Override
            public void onComplete() {

            }
        };


        if (missingThumbnailMap.size() != 0) {
            // running in thread on first run causes display issues
            if (missingThumbnailMap.size() < shows.size())
                notification.run();
            else
                updateThumbnails();

        }


        sortContainer(SORT.ALPHABETIC);
        setOptionalContainerVisibility(true);

        //adding genre tags to the menu
        genreTags.sort(String::compareToIgnoreCase);
        for (String tag : genreTags) {
            if (!tag.equals(""))
                genreTagFlowContainer.getChildren().add(createTagButton(tag));
        }

        studioTags.sort(String::compareToIgnoreCase);
        for (String tag : studioTags) {
            if (tag != null && !tag.equals(""))
                studioTagFlowContainer.getChildren().add(createTagButton(tag));
        }


        //setting the menu behaviour
        if (WindowsManager.getCurrentScreenPosition() < 1) {
            Utils.toggleVisibility(menuBack);
        } else {
            menuBack.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                    WindowsManager.goToPreviousScene((Stage) root.getScene().getWindow()));
        }


        //checking to see if the mouse left the window to rest ctrl
        root.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> isCtrlHeld = false);

        root.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent keyEvent) -> isCtrlHeld = false);


        menuAll.setOnMouseClicked(event -> WindowsManager.refreshScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW_CATALOG, Strings.APP_NAME, true));

        menuUpdate.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.runLater(() ->
                ShowInfoCreator.updateJsonForAllShows(() ->
                        WindowsManager.refreshScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW_CATALOG, Strings.APP_NAME, true))));


        menuStudioTag.setOnMouseClicked(event -> {
            Utils.toggleVisibility(studioTagFlowContainer);
            Utils.setVisibility(genreTagFlowContainer, false);
        });

        menuGenreTag.setOnMouseClicked(event -> {
            Utils.toggleVisibility(genreTagFlowContainer);
            Utils.setVisibility(studioTagFlowContainer, false);
        });

        mainScrollPane.setOnMouseEntered(event -> {
            Utils.setVisibility(genreTagFlowContainer, false);
            Utils.setVisibility(studioTagFlowContainer, false);
        });


        menuSearch.textProperty().addListener((observableValue, oldText, newText) -> {

            if (newText.trim().isEmpty()) {

                allTitleLabel.setText(Strings.TITLE_ALL_SHOWS + titleModifier);
                setOptionalContainerVisibility(true);
                tagContainer.setAllTagVisibility(true);

            } else {

                setOptionalContainerVisibility(false);
                Utils.setVisibility(genreTagFlowContainer, false);
                Utils.setVisibility(studioTagFlowContainer, false);

                if (root.lookup("#" + Strings.ID_SELECTED_TAG) != null)
                    root.lookup("#" + Strings.ID_SELECTED_TAG).setId(Strings.ID_UNSELECTED_TAG);

                tagContainer.searchForTagsWithTitle(newText);
                allTitleLabel.setText(newText + titleModifier);
            }


            if (allContainer.getHeight() > 1500) {
                Utils.setVisibility(navigationArrow, true);
            } else {
                Utils.setVisibility(navigationArrow, false);
            }

        });


        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {

            if (KeyboardShortcuts.SEARCH.match(keyEvent)) {
                menuSearch.setText("");
                menuSearch.requestFocus();
            } else if (KeyboardShortcuts.SEARCH_SELECT.match(keyEvent)) {
                if (!menuSearch.getText().trim().isEmpty()) {
                    for (Node show : allContainer.getChildren()) {
                        if (show.isVisible() && show instanceof VBox) {
                            ((VBox) show).getChildren().get(0).fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED,
                                    show.getLayoutX(), show.getLayoutY(), show.getLayoutX(), show.getLayoutY(),
                                    MouseButton.PRIMARY, 1, true, true, true,
                                    true, true, true, true,
                                    true, true, true, null));

                            break;
                        }
                    }
                }
            } else if (KeyboardShortcuts.REFRESH.match(keyEvent) || KeyboardShortcuts.REFRESH_ALTERNATIVE.match(keyEvent)) {
                WindowsManager.refreshScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW_CATALOG, Strings.APP_NAME, true);
            } else if (KeyboardShortcuts.SORT.match(keyEvent)) {
                currentSort = SORT.values()[(currentSort.ordinal() + 1) % SORT.values().length];
                sortContainer(currentSort);
            } else if (KeyboardShortcuts.CHANGE_THEME.match(keyEvent)) {
                root.getStylesheets().remove(ProjectPaths.JAR_CSS_FOLDER_PATH + user.getThemeName(true));
                user.setTheme(User.THEME.values()[((user.getTheme().ordinal() + 1) % User.THEME.values().length)]);
                root.getStylesheets().add(ProjectPaths.JAR_CSS_FOLDER_PATH + user.getThemeName(true));

                headerImage.setStyle("-fx-background-image: url('" + ProjectPaths.getInstance().getHeaderFolder() + user.getThemeName(false) + ".jpg" + "');");
            } else if (KeyboardShortcuts.UPDATE_FILES.match(keyEvent)) {
                Platform.runLater(ShowInfoCreator::updateJsonAndHtmlFilesForPastYear);
            } else if (keyEvent.isControlDown()) {
                isCtrlHeld = true;
            }
        });


        //creating the parallax background effect
        mainScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {

            if (allContainer.getHeight() > 1500) {
                Utils.setVisibility(navigationArrow, true);
            } else {
                Utils.setVisibility(navigationArrow, false);
            }

            updateParallax();
        });


        showContainerRoot.setOnScroll(event -> mainScrollPane.setVvalue(mainScrollPane.getVvalue() + -(event.getDeltaY() * 4) / 100000));
        navigationArrow.setOnMouseClicked(event -> {
            if (mainScrollPane.getVvalue() >= 0.5) {
                scrollToPosition(0);
            } else {
                scrollToPosition(1);
            }

        });


        //this minimizes a spacing bug
        airingContainer.prefWidthProperty().bind(root.widthProperty());
        watchingContainer.prefWidthProperty().bind(root.widthProperty());
        allContainer.prefWidthProperty().bind(root.widthProperty());
        showContainerRoot.prefWidthProperty().bind(root.widthProperty());
        mainScrollPane.prefWidthProperty().bind(root.widthProperty());
    }


    private void updateThumbnails(){
        Iterator iterator = missingThumbnailMap.entrySet().iterator();
        int showCount = 1;
        int size = missingThumbnailMap.size();

        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            ShowInfo showInfo =  (ShowInfo) pair.getValue();
            updateAlertText(showCount, size, showInfo.getTitle());
            showCount++;
            createThumbnail(showInfo, (ImageView) pair.getKey());
            iterator.remove();
        }
    }

    private static void updateAlertText(int position, int total, String showName){
        String message = "Created " + position + "/" + total + ".\n";
        message += "\nCreating thumbnail for " + showName + "...";
        String finalMessage = message;
        Platform.runLater(() -> notification.setText(finalMessage));
    }

    private void sortContainer(SORT sort) {

        ObservableList<Node> workingCollection = FXCollections.observableArrayList(
                allContainer.getChildren()
        );
        ObservableList<Node> workingCollectionWatching = FXCollections.observableArrayList(
                watchingContainer.getChildren()
        );

        //removing the title modifier from the title for cases where title is not the default title
        allTitleLabel.setText(allTitleLabel.getText().replace(titleModifier, ""));
        watchingTitleLabel.setText(watchingTitleLabel.getText().replace(titleModifier, ""));
        titleModifier = " ~ " + Utils.capitalize(sort.toString());

        if (sort == SORT.ALPHABETIC) {
            Collections.sort(workingCollection, new AlphabeticalComparator());
            Collections.sort(workingCollectionWatching, new AlphabeticalComparator());
            titleModifier = "";
        } else if (sort == SORT.DATE) {
            Collections.sort(workingCollection, new DateComparator());
            Collections.sort(workingCollectionWatching, new DateComparator());
        } else if (sort == SORT.RATING) {
            Collections.sort(workingCollection, new RatingComparator());
            Collections.sort(workingCollectionWatching, new RatingComparator());
        }

        allTitleLabel.setText(allTitleLabel.getText() + titleModifier);
        watchingTitleLabel.setText(watchingTitleLabel.getText() + titleModifier);

        allContainer.getChildren().setAll(workingCollection);
        watchingContainer.getChildren().setAll(workingCollectionWatching);

    }


    private void setOptionalContainerVisibility(boolean visible) {

        if (visible) {
            if (User.getInstance().getWatchingListSize() != 0){
                Utils.setVisibility(mainWatchingContainer, watchingContainer, true);
            }else {
                Utils.setVisibility(mainWatchingContainer, watchingContainer, false);
            }
            if(User.getInstance().isShowAiring() && airingShows.size() != 0) {
                Utils.setVisibility(mainAiringContainer, airingContainer, true);
            } else {
                Utils.setVisibility(mainAiringContainer, airingContainer, false);
            }

        } else {
            Utils.setVisibility(mainWatchingContainer, watchingContainer, false);
            Utils.setVisibility(mainAiringContainer, airingContainer, false);
        }
    }

    private Button createTagButton(String tag) {
        Button button = new Button(tag);
        button.setId(Strings.ID_UNSELECTED_TAG);
        button.setOnAction(event -> {
            tagContainer.selectTag(tag);

            if (root.lookup("#" + Strings.ID_SELECTED_TAG) != null)
                root.lookup("#" + Strings.ID_SELECTED_TAG).setId(Strings.ID_UNSELECTED_TAG);


            if (allTitleLabel.getText().equals(tag + titleModifier)) {
                allTitleLabel.setText(Strings.TITLE_ALL_SHOWS + titleModifier);
                setOptionalContainerVisibility(true);
                button.setId(Strings.ID_UNSELECTED_TAG);
            } else {

                setOptionalContainerVisibility(false);
                allTitleLabel.setText(tag.replace("_", "") + titleModifier);
                button.setId(Strings.ID_SELECTED_TAG);
            }
        });

        if (WindowsManager.getWindowParams().equals(tag)) {
            button.fire();
        }

        return button;
    }

    private VBox createShowContainer(ShowInfo show) {

        ImageView showCover = new ImageView();
        showCover.setFitWidth(Thumbnail.THUMBNAIL_SIZE.SMALL.getWidth());
        showCover.setFitHeight(Thumbnail.THUMBNAIL_SIZE.SMALL.getHeight());
        showCover.setPreserveRatio(true);
        showCover.setId(Strings.ID_SHOW_COVER);

        Text showTitle = new Text(show.getTitle());
        showTitle.getStyleClass().add(Strings.CLASS_SHOW_TITLE);
        showTitle.setWrappingWidth(200);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setSpacing(5);
        vBox.getChildren().add(showCover);
        vBox.getChildren().add(showTitle);

        if (thumbnailMap.containsKey(show.getTitle())) {
            showCover.setImage(thumbnailMap.get(show.getTitle()));
        } else {
            missingThumbnailMap.put(showCover, show);
        }

        showCover.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            if (isCtrlHeld) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(ProjectPaths.getInstance().convertToNetWorkPath(show.getPathToShow())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                WindowsManager.goToNewScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW, show.getTitle());
            }
        });

        vBox.setUserData(show);
        vBox.setManaged(true);
        return vBox;

    }

    private void createThumbnail(ShowInfo show, ImageView showCover) {

        //used for watching and airing thumbnails
        if (thumbnailMap.containsKey(show.getTitle())) {
            showCover.setImage(thumbnailMap.get(show.getTitle()));
            return;
        }

        BufferedImage bufferedImage = Thumbnail.createThumbnail(ProjectPaths.getInstance().getCoverImagesFolder(), show, Thumbnail.THUMBNAIL_SIZE.LARGE, false);

        if (bufferedImage == null)
            bufferedImage = Thumbnail.createThumbnail(ProjectPaths.getInstance().getCoverImagesFolder(), Strings.DEFAULT_THUMBNAIL, Thumbnail.THUMBNAIL_SIZE.LARGE, false);

        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

        showCover.setImage(image);
        thumbnailMap.put(show.getTitle(), image);
    }

    private void updateParallax() {

        double scrollDist = (showContainerRoot.localToScreen(showContainerRoot.getBoundsInLocal()).getMinY()) / 8;

        if (!Double.isNaN(scrollDist)) {
            headerImage.setTranslateY(90 - scrollDist);
        }

        if (mainScrollPane.getVvalue() >= 0.5) {
            if (isScrollingUp) {
                navigationArrow.setStyle("-fx-background-image: url(" + ProjectPaths.JAR_ICONS_FOLDER_PATH + Strings.ICON_ARROW_UP + ");");
                isScrollingUp = false;
            }
        } else {
            if (!isScrollingUp) {
                navigationArrow.setStyle("-fx-background-image: url(" + ProjectPaths.JAR_ICONS_FOLDER_PATH +  Strings.ICON_ARROW_DOWN + ");");
                isScrollingUp = true;
            }
        }

    }

    private void scrollToPosition(double position) {

        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(mainScrollPane.vvalueProperty(), position))
        );

        animation.play();

    }
}




