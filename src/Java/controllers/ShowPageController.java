package Java.controllers;

import Java.*;
import Java.comparators.NaturalOrderComparator;
import Java.models.*;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import Java.tools.WindowsManager;
import Java.web.ShowInfoCreator;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.net.URL;
import java.util.*;

public class ShowPageController implements Initializable {


    @FXML
    private ImageView cover, menuBack, menuAll, menuWatching, menuFolder, menuWikipedia, menuUpdate, menuMal;
    @FXML
    private VBox episodeContainer, sidebarContainer, recommendations, mainContainer;
    @FXML
    private AnchorPane root, backgroundImage, rootContainer, backgroundImageUnderlay;
    @FXML
    private Label title, rating, ageRating, date, ratingLabel, tagsLabel, ageRatingLabel, dateLabel, recommendationsLabel, studioLabel;
    @FXML
    private Text mainTitle;
    @FXML
    private FlowPane tags, folderContainer;
    @FXML
    private Text plot;
    @FXML
    private Button navigationArrow;
    @FXML
    private javafx.scene.control.Button studio;
    @FXML
    private javafx.scene.control.ScrollPane mainScrollPane;


    private ArrayList<String> folderNames = new ArrayList<>();
    private TagContainer tagEpisodeContainer = new TagContainer();
    private String watchingFolder = Strings.NULL;
    private double sidebarTopPadding;
    private boolean isWatching = false;
    private boolean scrolledToWatching = false;
    private boolean isScrollingUp = true;
    private String wikiLink;
    private ShowInfo showInfo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        User user = User.getInstance();

        root.getStylesheets().add(ProjectPaths.JAR_CSS_FOLDER_PATH + user.getThemeName(true));
        showInfo = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonShowFolder(), WindowsManager.getWindowParams() + ".json", ShowInfo.class);

        //setting values
        mainTitle.setFontSmoothingType(FontSmoothingType.GRAY);
        if (!showInfo.getJapaneseTitle().equals(Strings.NULL)) {
            mainTitle.setText(showInfo.getJapaneseTitle());
        } else {
            mainTitle.setText(showInfo.getTitle());
        }


        plot.wrappingWidthProperty().bind(mainContainer.widthProperty().subtract(30));
        plot.setText("     " + Jsoup.parse(showInfo.getPlot()).text());
        title.setText(showInfo.getTitle());
        studio.setText(showInfo.getStudio());
        rating.setText(showInfo.getRating());
        ageRating.setText(showInfo.getAgeRating());
        date.setText(showInfo.getAirDate());
        sidebarTopPadding = sidebarContainer.getPadding().getTop();
        wikiLink = showInfo.getWikiLink();

        Set<UnicodeBlock> japaneseUnicodeBlocks = new HashSet<UnicodeBlock>() {{
            add(UnicodeBlock.HIRAGANA);
            add(UnicodeBlock.KATAKANA);
            add(UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
            add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        }};

        //checking to see if the text size should be smaller
        float realTitleSize = 0;
        for (char character : mainTitle.getText().toCharArray()) {
            if (japaneseUnicodeBlocks.contains(UnicodeBlock.of(character)))
                realTitleSize += 2;
            else
                realTitleSize += 1;
        }

        if (realTitleSize > 45) {
            if (realTitleSize >= 110)
                mainTitle.setStyle("-fx-font-size:" + (30) + ";");
            else
                mainTitle.setStyle("-fx-font-size:" + (110 - realTitleSize) + ";");
        }


        //hiding labels from side bar if info is missing
        if (showInfo.getPlot().equals(Strings.NULL))
            plot.setText("");

        if (showInfo.getWikiLink().equals(Strings.NULL))
            Utils.setVisibility(menuWikipedia, false);

        if (showInfo.getMalLink().equals(Strings.NULL))
            Utils.setVisibility(menuMal, false);

        if (showInfo.getStudio().equals(Strings.NULL)) {
            Utils.setVisibility(studio, false);
            Utils.setVisibility(studioLabel, false);
        }

        if (showInfo.getRecommendations().isEmpty()) {
            Utils.setVisibility(recommendations, false);
            Utils.setVisibility(recommendationsLabel, false);
        }

        if (showInfo.getAgeRating().equals("None") || showInfo.getAgeRating().equals(Strings.NULL)) {
            Utils.setVisibility(ageRating, false);
            Utils.setVisibility(ageRatingLabel, false);
        }

        if (showInfo.getAirDate().equals(Strings.NULL)) {
            Utils.setVisibility(date, false);
            Utils.setVisibility(dateLabel, false);
        }

        if (showInfo.getRating().equals(Strings.NULL)) {
            Utils.setVisibility(rating, false);
            Utils.setVisibility(ratingLabel, false);
        }

        if (showInfo.getVisibleTags().isEmpty()) {
            Utils.setVisibility(tags, false);
            Utils.setVisibility(tagsLabel, false);
        }


        if (user.isWatchingShow(showInfo.getTitle())) {
            menuWatching.setImage(new Image(ProjectPaths.JAR_ICONS_FOLDER_PATH + Strings.ICON_FAVORITE, true));
            isWatching = true;
        }


        //settings background image
        Image image = new Image("file:////" + ProjectPaths.createCleanPath(ProjectPaths.getInstance().getBackgroundImagesFolder(), showInfo.getDefaultTitle() + ".jpg"), false);

        if (image.isError())
            image = new Image("file:////" + ProjectPaths.createCleanPath(ProjectPaths.getInstance().getBackgroundImagesFolder(), Strings.DEFAULT_THUMBNAIL + ".jpg"), false);


        BackgroundSize bgSize = new BackgroundSize(2000, 2600, false, false, false, false);
        BackgroundPosition bgPosition = new BackgroundPosition(null, -41, false, null, -290, false);
        BackgroundImage bgImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, bgPosition, bgSize);
        backgroundImage.getStyleClass().clear();
        backgroundImage.setBackground(new Background(bgImage));

        Color color = image.getPixelReader().getColor((int) image.getWidth() - 10, 0);
        backgroundImageUnderlay.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));


        //settings cover image
        BufferedImage coverThumbnail = Thumbnail.createThumbnail(ProjectPaths.getInstance().getCoverImagesFolder(), showInfo, Thumbnail.THUMBNAIL_SIZE.LARGE, false);
        if (coverThumbnail == null)
            coverThumbnail = Thumbnail.createThumbnail(ProjectPaths.getInstance().getCoverImagesFolder(), Strings.DEFAULT_THUMBNAIL, Thumbnail.THUMBNAIL_SIZE.LARGE, false);
        cover.setImage(SwingFXUtils.toFXImage(Objects.requireNonNull(coverThumbnail), null));


        //creating tags
        for (String tag : showInfo.getVisibleTags()) {
            Button tagButton = new Button();

            //cutting of tags that are too long
            if (tag.length() > 40) {
                String tagCount = tag.split("\\(")[1];
                String tmpTag = tag.split("\\(")[0].substring(0, (40 - tagCount.length()));
                tmpTag += "... (";
                tmpTag += tagCount;
                tagButton.setText(tmpTag);
            } else {
                tagButton.setText(tag);
            }

            tagButton.setOnMouseClicked(event -> WindowsManager.goToNewScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW_CATALOG, tag));
            tags.getChildren().add(tagButton);
        }

        //creating recommendations
        for (ShowRecommendation showRecommendation : showInfo.getRecommendations()) {

            recommendations.setSpacing(10);

            Button label = new Button();

            if (!showRecommendation.getMyTitle().equals(Strings.NULL))
                label.setText(showRecommendation.getMyTitle());
            else
                label.setText(showRecommendation.getTitle());

            if (showRecommendation.isDownloaded()) {
                label.setId(Strings.ID_RECOMMENDED_SHOW);
                label.setOnMouseClicked(event -> WindowsManager.goToNewScene((Stage) root.getScene().getWindow(),
                        Strings.LAYOUT_SHOW, showRecommendation.getMyTitle()));

            } else {
                label.setId(Strings.ID_RECOMMENDED_SHOW_MISSING);
                label.setOnMouseClicked(event -> Utils.openLink(showRecommendation.getLink()));
            }

            recommendations.getChildren().add(label);

        }


        studio.setOnMouseClicked(event -> WindowsManager.goToNewScene((Stage) root.getScene().getWindow(),
                Strings.LAYOUT_SHOW_CATALOG, showInfo.getStudio()));

        menuBack.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                WindowsManager.goToPreviousScene((Stage) root.getScene().getWindow()));

        menuAll.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                WindowsManager.goToNewScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW_CATALOG, Strings.APP_NAME));

        menuMal.setOnMouseClicked(event -> Utils.openLink(showInfo.getMalLink()));

        menuWikipedia.setOnMouseClicked(event -> Utils.openLink(wikiLink));

        menuWatching.setOnMouseClicked(event -> {

            if (isWatching) {
                menuWatching.setImage(new Image(ProjectPaths.JAR_ICONS_FOLDER_PATH + Strings.ICON_FAVORITE_BORDER, true));
                if (root.lookup("#" + Strings.ID_WATCHING_EPISODE) != null) {
                    root.lookup("#" + Strings.ID_WATCHING_EPISODE).getStyleClass().remove(Strings.CLASS_EPISODE_CONTAINER_WATCHING);
                    root.lookup("#" + Strings.ID_WATCHING_EPISODE).setId("");
                }
                isWatching = false;
            } else {
                menuWatching.setImage(new Image(ProjectPaths.JAR_ICONS_FOLDER_PATH + Strings.ICON_FAVORITE, true));
                isWatching = true;
            }

            user.toggleWatchingShow(showInfo.getTitle());

        });


        //creating episode links
        boolean darken = true;
        String watchingEpisode = user.getWatchingSelectedEpisode(showInfo.getTitle());
        for (ArrayList<String> episode : showInfo.getEpisodes()) {

            if (!folderNames.contains(episode.get(ShowInfo.FOLDER_NAME)))
                folderNames.add(episode.get(ShowInfo.FOLDER_NAME));

            Pane iconPane = new Pane();
            iconPane.setPrefWidth(50);
            iconPane.setPrefHeight(50);
            iconPane.getStyleClass().add(Strings.CLASS_EPISODE_CONTAINER_ICONS);

            Pane pane = new Pane();
            pane.getChildren().add(iconPane);
            pane.setMinHeight(50);
            pane.setPrefHeight(50);

            Label name = new Label(episode.get(ShowInfo.FILE_NAME));
            name.setId(Strings.ID_EPISODE_LINK);
            name.setWrapText(true);
            name.layoutXProperty().bind(pane.widthProperty().subtract(name.widthProperty()).divide(2)); //centering the link
            name.layoutYProperty().bind(pane.heightProperty().subtract(name.heightProperty()).divide(2)); //centering the link

            pane.getChildren().add(name);

            if (darken)
                pane.getStyleClass().add(Strings.CLASS_EPISODE_CONTAINER_DARK);
            else
                pane.getStyleClass().add(Strings.CLASS_EPISODE_CONTAINER_LIGHT);

            darken = !darken;

            if (user.getEpisodeTag(episode.get(ShowInfo.FILE_NAME)) != null)
                iconPane.getStyleClass().add(user.getEpisodeTag(episode.get(ShowInfo.FILE_NAME)));

            if (isWatching && episode.get(ShowInfo.FILE_NAME).equals(watchingEpisode)) {
                pane.getStyleClass().add(Strings.CLASS_EPISODE_CONTAINER_WATCHING);
                pane.setId(Strings.ID_WATCHING_EPISODE);
                watchingFolder = episode.get(ShowInfo.FOLDER_NAME);
            }

            name.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                if (isWatching) {
                    if (root.lookup("#" + Strings.ID_WATCHING_EPISODE) != null) {
                        root.lookup("#" + Strings.ID_WATCHING_EPISODE).getStyleClass().remove(Strings.CLASS_EPISODE_CONTAINER_WATCHING);
                        root.lookup("#" + Strings.ID_WATCHING_EPISODE).setId("");
                    }

                    user.updateWatchingShow(showInfo.getTitle(), episode.get(ShowInfo.FILE_NAME));

                    name.requestFocus();
                    pane.getStyleClass().add(Strings.CLASS_EPISODE_CONTAINER_WATCHING);
                    pane.setId(Strings.ID_WATCHING_EPISODE);
                }

                if (Desktop.isDesktopSupported()) {
                    new Thread(() -> {
                        try {
                            String episodeLink = ProjectPaths.getInstance().convertToNetWorkPath(episode.get(1));
                            Desktop.getDesktop().open(new File(episodeLink));
                        } catch (IOException e1) {
                            System.out.println("Open File: " + e1);
                            e1.printStackTrace();
                        }
                    }).start();
                }

            });

            tagEpisodeContainer.getTags().add(new TagObject(episode.get(ShowInfo.FOLDER_NAME), pane));
            episodeContainer.getChildren().add(pane);

        }

        //creating show folders
        Button watchingFolderButton = null;
        if (folderNames.size() != 1) {

            folderNames.sort(new NaturalOrderComparator<>(true));

            for (String folder : folderNames) {

                Button folderButton = new Button();
                folderButton.setId(Strings.ID_UNSELECTED_FOLDER_BUTTON);
                folderButton.setText(folder);

                folderButton.setOnAction(event -> {

                    tagEpisodeContainer.selectTag(folder);

                    if (mainContainer.lookup("#" + Strings.ID_SELECTED_FOLDER_BUTTON) == null) {
                        folderButton.setId(Strings.ID_SELECTED_FOLDER_BUTTON);

                    } else if (mainContainer.lookup("#" + Strings.ID_SELECTED_FOLDER_BUTTON) == folderButton) {
                        folderButton.setId(Strings.ID_UNSELECTED_FOLDER_BUTTON);
                    } else {
                        mainContainer.lookup("#" + Strings.ID_SELECTED_FOLDER_BUTTON).setId(Strings.ID_UNSELECTED_FOLDER_BUTTON);
                        folderButton.setId(Strings.ID_SELECTED_FOLDER_BUTTON);
                    }

                });


                folderContainer.getChildren().add(folderButton);

                if (watchingFolderButton == null)
                    watchingFolderButton = folderButton;

                if (isWatching && folder.contains(watchingFolder)) {
                    watchingFolderButton = folderButton;
                } else if ((!isWatching || watchingFolder.equals(Strings.NULL)) &&
                        !watchingFolderButton.getText().equals(Strings.WATCHING_FOLDER_DEFAULT) && !watchingFolderButton.getText().contains(Strings.WATCHING_FOLDER_PART_1)
                        && (folder.contains(Strings.WATCHING_FOLDER_DEFAULT) || folder.contains(Strings.WATCHING_FOLDER_PART_1))) {

                    watchingFolderButton = folderButton;
                }

            }

            if (watchingFolderButton == null)
                watchingFolderButton = new Button();

            watchingFolderButton.fire();
        } else {
            Utils.setVisibility(folderContainer, false);
        }


        navigationArrow.setOnMouseClicked(event -> {
            if (mainScrollPane.getVvalue() >= 0.5)
                scrollToPosition(0, 0.5);
            else
                scrollToPosition(1, 0.5);
        });


        menuUpdate.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Platform.runLater(() ->
                ShowInfoCreator.updateJsonForShow(showInfo, () ->
                        WindowsManager.refreshScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW, showInfo.getTitle()))));

        root.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {

            if (KeyboardShortcuts.REFRESH.match(keyEvent) || KeyboardShortcuts.REFRESH_ALTERNATIVE.match(keyEvent)) {
                WindowsManager.refreshScene((Stage) root.getScene().getWindow(), Strings.LAYOUT_SHOW);
            } else if (KeyboardShortcuts.CHANGE_THEME.match(keyEvent)) {

                root.getStylesheets().remove(ProjectPaths.JAR_CSS_FOLDER_PATH + user.getThemeName(true));
                user.setTheme(User.THEME.values()[((user.getTheme().ordinal() + 1) % User.THEME.values().length)]);
                root.getStylesheets().add(ProjectPaths.JAR_CSS_FOLDER_PATH + user.getThemeName(true));

            } else if (KeyboardShortcuts.OPEN_MAL.match(keyEvent)) {
                Utils.openLink(showInfo.getMalLink());
            } else if (KeyboardShortcuts.OPEN_ANIDB.match(keyEvent)) {
                Utils.openLink(showInfo.getAniDbLink());
            }

        });

        menuFolder.setOnMouseClicked(event -> new Thread(() -> {
            try {
                Desktop.getDesktop().open(new File(ProjectPaths.getInstance().convertToNetWorkPath(showInfo.getPathToShow())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());

        mainScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {

            if (mainScrollPane.getVvalue() >= 0.5) {
                if (isScrollingUp) {
                    navigationArrow.setStyle("-fx-background-image: url(" + ProjectPaths.JAR_ICONS_FOLDER_PATH + Strings.ICON_ARROW_UP + ");");
                    isScrollingUp = false;
                }
            } else {
                if (!isScrollingUp) {
                    navigationArrow.setStyle("-fx-background-image: url(" + ProjectPaths.JAR_ICONS_FOLDER_PATH + Strings.ICON_ARROW_DOWN + ");");
                    isScrollingUp = true;
                }
            }
            updateParallax();
        });

        mainContainer.heightProperty().addListener((observable, oldValue, newValue) -> {

            //scrolling to watching episode if it's not visible
            if (isWatching && !scrolledToWatching) {
                Node node = mainContainer.lookup("#" + Strings.ID_WATCHING_EPISODE);
                if (node != null) {
                    Platform.runLater(() -> {
                        if (-(getVisibleBounds(node).getMinY()) > (getVisibleBounds(node).getHeight() - 25)) {
                            Bounds boundsInScene = (getVisibleBounds(node));
                            double scrollDistPercent = (-boundsInScene.getMinY() / (mainScrollPane.getContent().getBoundsInLocal().getHeight() - 350));
                            scrollToPosition(scrollDistPercent, (0.5 * scrollDistPercent) + 0.1);
                        }
                        scrolledToWatching = true;
                    });
                }
            }

            if (mainContainer.getHeight() > 1500) {
                Utils.setVisibility(navigationArrow, true);
            } else {
                Utils.setVisibility(navigationArrow, false);
            }

            updateParallax();

        });


        Utils.setVisibility(plot, true);
        mainContainer.setOnScroll(event -> mainScrollPane.setVvalue(mainScrollPane.getVvalue() + -(event.getDeltaY() * 4) / 5000));


    }


    private void updateParallax() {

        double scrollDist = (rootContainer.localToScreen(rootContainer.getBoundsInLocal()).getMinY()) / 8;

        backgroundImage.setTranslateY(10 - scrollDist);

        if (sidebarTopPadding - (scrollDist * 4) < 40)
            sidebarContainer.setPadding(new Insets(sidebarTopPadding - (scrollDist * 4), 0, 0, 0));
        else
            sidebarContainer.setPadding(new Insets(40, 0, 0, 0));

    }

    private static Bounds getVisibleBounds(Node aNode)
    {
        // If node not visible, return empty bounds
        if(!aNode.isVisible()) return new BoundingBox(0,0,-1,-1);

        // If node has clip, return clip bounds in node coords
        if(aNode.getClip()!=null) return aNode.getClip().getBoundsInParent();

        // If node has parent, get parent visible bounds in node coords
        Bounds bounds = aNode.getParent()!=null? getVisibleBounds(aNode.getParent()) : null;
        if(bounds!=null && !bounds.isEmpty()) bounds = aNode.parentToLocal(bounds);
        return bounds;
    }


    private void scrollToPosition(double position, double seconds) {

        Animation animation = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        new KeyValue(mainScrollPane.vvalueProperty(), position))
        );

        animation.play();
    }

}
