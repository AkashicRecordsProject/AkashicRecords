package Java.web;


import Java.Notification;
import Java.Strings;
import Java.models.*;
import Java.comparators.NaturalOrderComparator;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import javafx.application.Platform;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ShowInfoCreator {

    private static HashMap<String, ShowInfo> showList = new HashMap<>();
    private static ArrayList<ShowInfo> showListNeedUpdating = new ArrayList<>();
    private static HashMap<String, Integer> tagList = new HashMap<>();
    private static HashMap<String, String> alternativeTitleList = new HashMap<>();
    private static ShowContainer showContainers = new ShowContainer();
    private static boolean HAS_EXTERNAL_SUBTITLES = false;
    private static boolean NEW_SHOW_ADDED = false;
    private final static int TAG_CUTOFF = 1;
    private static Notification notification;
    private static final int WAIT_TIME = 2000;

    public interface OnCompleteListener {
        void onComplete();
    }

    private ShowInfoCreator() {

    }

    public static void updateJsonAndHtmlFilesForPastYear() {

            ArrayList<ShowInfo> shows = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonContainersFolder(), Strings.JSON_ALL_SHOWS, ShowContainer.class).getShows();

            String season = Utils.getCurrentSeason(true);
            String year = season.split(" ")[1];
            season = season.split(" ")[0];

            int seasonPosition;

            switch (season) {
                case "Spring":
                    seasonPosition = 0;
                    break;
                case "Summer":
                    seasonPosition = 1;
                    break;
                case "Fall":
                    seasonPosition = 2;
                    break;
                default:
                    seasonPosition = 3;
                    break;
            }

            String[] seasonList = {"Spring", "Summer", "Fall", "Winter"};
            String startYear = year;

            // getting the shows that have been released in the last year
            for (int i = 0; i < seasonList.length; i++) {

                if (seasonList[seasonPosition].equals("Fall") && i != 0)
                    year = String.valueOf(Integer.valueOf(startYear) - 1);

                seasonList[seasonPosition] = seasonList[seasonPosition] += " " + year;

                seasonPosition--;
                if (seasonPosition == -1)
                    seasonPosition = 3;

            }

            System.out.println(season + " " + Arrays.toString(seasonList));

            //updating the html sources for the shows
            ArrayList<ShowInfo> updatingShows = new ArrayList<>();

            for (ShowInfo tmpShow : shows) {
                for (String tmpDate : seasonList) {
                    if (tmpShow.getAirDate().contains(tmpDate) || tmpShow.getAirDate().equalsIgnoreCase("0")) {
                        updatingShows.add(tmpShow);
                    }
                }
            }


        notification = new Notification(Strings.DIALOG_SHOW_UPDATE_LAST_YEAR_TITLE, Strings.DIALOG_SHOW_UPDATE_LAST_YEAR_HEADER, Strings.DIALOG_SHOW_UPDATE_LAST_YEAR_TEXT) {
            @Override
            public void onRun() {
                WebSearch webSearch = new WebSearch();
                int position = 1;

                for(ShowInfo  showInfo: updatingShows) {
                    System.out.println(showInfo.getTitle() + " " + showInfo.getAirDate());

                    updateLoadingText(position, updatingShows.size(), showInfo.getTitle());
                    webSearch.createHTMLFilesFromLink(showInfo, true);
                    position++;

                    File file = new File(ProjectPaths.getInstance().getJsonShowFolder() + showInfo.getTitle() + ".json");
                    file.delete();
                    try {
                        Thread.sleep(WAIT_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onComplete() {
                updateJsonForAllShows(() -> System.out.println("updateJsonForAllShows onComplete"));
            }
        };

        notification.run();

    }

    public static void updateJsonForShow(ShowInfo showInfo, OnCompleteListener onCompleteListener) {

        HashMap<String, String> episodesPathList = new HashMap<>();
        ArrayList<String> episodeList = new ArrayList<>();
        ArrayList<File> animeFolders = new ArrayList<>();
        tagList = new HashMap<>();
        showContainers.setShows(null);

        createTags();

        animeFolders.add(new File(ProjectPaths.getInstance().convertToNetWorkPath(showInfo.getPathToShow().replace(showInfo.getTitle(),""))));
        updateShowList(animeFolders, episodeList, episodesPathList, showInfo.getTitle());

        onCompleteListener.onComplete();
    }


    private static void updateLoadingText(int position, int total, String showName){
        int time = ((total-position)*WAIT_TIME / 60000);

        String message = "Found " + position + "/" + total + ".\nEstimated time to completion is ";

        if(time == 0)
            message += "under a minute.";
        else
            message += time + " minutes.";

        message += "\nSearching for " + showName + "...";
        String finalMessage = message;

        Platform.runLater(() -> notification.setText(finalMessage));
    }

    public static void updateJsonForAllShows(OnCompleteListener onCompleteListener) {

        HashMap<String, String> episodesPathList = new HashMap<>();
        ArrayList<String> episodeList = new ArrayList<>();
        ArrayList<File> animeFolders = new ArrayList<>();
        tagList = new HashMap<>();


        notification = new Notification(Strings.DIALOG_SHOW_UPDATE_TITLE, Strings.DIALOG_SHOW_UPDATE_HEADER, Strings.DIALOG_SHOW_UPDATE_TEXT) {
            @Override
            public void onRun() {
                createTags();
                updateAllShows(animeFolders, episodeList, episodesPathList);
                Platform.runLater(() -> {
                    notification.setTitle(Strings.DIALOG_LOADING_TITLE);
                    notification.setHeaderText(Strings.DIALOG_LOADING_HEADER);
                });

                createShowJsonFromWeb(animeFolders, episodeList, episodesPathList);
            }

            @Override
            public void onComplete() {
            onCompleteListener.onComplete();
            }
        };

        notification.run();

    }


    private static void createShowJsonFromWeb(ArrayList<File> animeFolders, ArrayList<String> episodeList, HashMap<String, String> episodesPathList){
        int numberOfShowUpdating = 1;

        if(showListNeedUpdating.size() != 0)
            updateLoadingText(numberOfShowUpdating, showListNeedUpdating.size(), showListNeedUpdating.get(0).getTitle());
        else
            updateLoadingText(numberOfShowUpdating, 1, "Show");

        for (ShowInfo showInfo : showListNeedUpdating) {
            try {
                int waitTime = 0;
                if (!new File(ProjectPaths.getInstance().getHtmlAnidb() + showInfo.getTitle()).exists()) {
                    new WebSearch().createHTMLFilesFromGoogleForAniDb(showInfo.getTitle());
                    waitTime = WAIT_TIME;
                }
                if (!new File(ProjectPaths.getInstance().getHtmlMal() + showInfo.getTitle()).exists()) {
                    new WebSearch().createHTMLFilesFromGoogleForMAL(showInfo.getTitle());
                    waitTime = WAIT_TIME;
                }

                createShowInfoObject(showInfo);
                updateLoadingText(numberOfShowUpdating, showListNeedUpdating.size(), showInfo.getTitle());
                numberOfShowUpdating++;
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        showListNeedUpdating.clear();

        if (NEW_SHOW_ADDED) {
            tagList = new HashMap<>();
            createTags();
            updateAllShows(animeFolders, episodeList, episodesPathList);
            createRecommendation();
            NEW_SHOW_ADDED = false;
        }

    }

    private static void updateAllShows(ArrayList<File> animeFolders, ArrayList<String> episodeList, HashMap<String, String> episodesPathList) {
        episodesPathList.clear();
        animeFolders.clear();
        episodeList.clear();

        showContainers.setShows(null);


        for(Path path : AkashicRecordsSettings.getInstance().getShowFolderPaths()){

            if(!AkashicRecordsSettings.getInstance().isShowFolderIncludesNetworkPaths()){
                animeFolders.add(new File(path.getLocalFolder()));
            } else {
                if(ProjectPaths.getInstance().getOS() == ProjectPaths.OS.WINDOWS)
                    animeFolders.add(new File(path.getWindowsNetworkFolder()));
                else if(ProjectPaths.getInstance().getOS() == ProjectPaths.OS.LINUX)
                    animeFolders.add(new File(path.getLinuxNetworkFolder()));
                else if(ProjectPaths.getInstance().getOS() == ProjectPaths.OS.MAC)
                    animeFolders.add(new File(path.getMacNetworkFolder()));
            }


        }

        if (!AkashicRecordsSettings.getInstance().getShowFolderPaths().get(0).getLocalFolder().equals(Strings.NULL))
            updateShowList(animeFolders, episodeList, episodesPathList, Strings.JSON_ALL_SHOWS);

    }


    private static void updateShowList(ArrayList<File> animeFolders, ArrayList<String> episodeList, HashMap<String, String> episodesPathList, String title) {

        ArrayList<String> shows = new ArrayList<>();
        String oldShowName = "";

        //going though all of the anime folders
        for (File animeFolder : animeFolders) {

            File[] listOfFiles = animeFolder.listFiles();

            if(listOfFiles == null) {
                System.out.println(animeFolder.getPath());
                continue;
            }
            //going though all shows in this folder
            for (File file : listOfFiles) {

//                checking if user is updating a single show
                if (!title.equals(Strings.JSON_ALL_SHOWS) && !title.equals(file.getName()))
                    continue;

                HAS_EXTERNAL_SUBTITLES = false;

                //adding episodes for the current show
                if (file.isFile()) {
                    if (isVideoFile(file.getName())) {
                        episodesPathList.put(file.getName(), ProjectPaths.getInstance().convertNetworkPathToLocal(file.getAbsolutePath()));
                        episodeList.add(file.getName());
                    }
                }
                //going through and sub folders and adding episodes
                else if (file.isDirectory() && !file.getName().substring(0, 1).equals(".")) {
                    shows.add(file.getName());
                    oldShowName = file.getName();
                    SearchDirectory(file.getAbsolutePath(), episodeList, episodesPathList);
                }

                // if folder contains any episodes creating a json object for it
                if (episodeList.size() != 0) {

                    ShowInfo showInfo = null;

                    //checking to see if json for show exists if not creating one
                    if (new File(ProjectPaths.getInstance().getJsonShowFolder() + oldShowName + ".json").exists()) {
                        showInfo = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonShowFolder(), oldShowName + ".json", ShowInfo.class);
                    }

                    if (showInfo != null && !showInfo.getMalLink().equals(Strings.NULL)) {
                        alternativeTitleList.put(showInfo.getMalTitle(), oldShowName);
                        showInfo.setPathToShow(ProjectPaths.getInstance().convertNetworkPathToLocal(file.getAbsolutePath()));
                        createJsonForShow(showInfo, episodeList, episodesPathList, HAS_EXTERNAL_SUBTITLES);
                        episodeList.clear();
                        showList.put(oldShowName, showInfo);

                    } else {
                        // create a JSON file with just the episode links in it while a more complete json files gets created in the background
                        final ShowInfo tmpShowInfo = new ShowInfo();
                        tmpShowInfo.setTitle(oldShowName);
                        tmpShowInfo.setPathToShow(ProjectPaths.getInstance().convertNetworkPathToLocal(file.getAbsolutePath()));
                        createJsonForShow(tmpShowInfo, episodeList, episodesPathList, HAS_EXTERNAL_SUBTITLES);
                        episodeList.clear();
                        showListNeedUpdating.add(tmpShowInfo);
                        showList.put(oldShowName, tmpShowInfo);
                    }
                }
            }
        }

        Collections.sort(shows);

        //creating showInfo for catalog container json
        ArrayList<ShowInfo> showInfoList = new ArrayList<>();
        for (String show : shows) {

                ShowInfo originalShow = showList.get(show);
                if(originalShow == null)
                    continue;

                ShowInfo tmpShow = new ShowInfo();
                tmpShow.setPathToShow(originalShow.getPathToShow());
                tmpShow.setTitle(originalShow.getTitle());
                tmpShow.setJapaneseTitle(originalShow.getJapaneseTitle());
                tmpShow.setMalTitle(originalShow.getMalTitle());
                tmpShow.setMalLink(originalShow.getMalLink());
                tmpShow.setDefaultTitle(originalShow.getDefaultTitle());
                tmpShow.setAniDbLink(originalShow.getAniDbLink());
                tmpShow.setWikiLink(originalShow.getWikiLink());
                tmpShow.setStudio(originalShow.getStudio());
                tmpShow.setRating(originalShow.getRating());
                tmpShow.setAgeRating(originalShow.getAgeRating());
                tmpShow.setAirDate(originalShow.getAirDate());
                tmpShow.setVisibleTags(originalShow.getVisibleTags());
                tmpShow.setPlot(null);
                tmpShow.setRecommendations(null);
                tmpShow.setHasExternalSubtitles(false);
                tmpShow.setEpisodes(null);
                tmpShow.setTags(null);
                showInfoList.add(tmpShow);
        }

        //creating catalog json
        if(title.equals(Strings.JSON_ALL_SHOWS)) {
            showContainers.setShows(showInfoList);
            showContainers.setTitle(title);
            Utils.saveJsonToFile(showContainers, ProjectPaths.getInstance().getJsonContainersFolder(), showContainers.getTitle());
        }

    }

    // recursively going though all sub folders and adding episodes
    private static void SearchDirectory(String path, ArrayList<String> episodeList, HashMap<String, String> episodesPathList) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (isVideoFile(file.getName())) {
                episodesPathList.put(file.getName(), ProjectPaths.getInstance().convertNetworkPathToLocal(file.getAbsolutePath()));
                episodeList.add(file.getName());
            } else if (file.isDirectory() && !file.getName().substring(0, 1).equals(".")) {
                SearchDirectory(file.getAbsolutePath(), episodeList, episodesPathList);
            } else if (isSubtitleFile(file.getName())) {
                HAS_EXTERNAL_SUBTITLES = true;
            }
        }
    }


    private static void createShowInfoObject(ShowInfo showInfo) {
        NEW_SHOW_ADDED = true;
        new HtmlParser().populateShowInfoFromHTML(showInfo.getTitle(), showInfo);
        Utils.saveJsonToFile(showInfo, ProjectPaths.getInstance().getJsonShowFolder(), showInfo.getTitle() + ".json");
    }


    private static void createJsonForShow(ShowInfo show, ArrayList<String> episodeList, HashMap<String, String> episodesPathList, boolean hasExternalSubtitles) {

        //sorting the episodes and tags
        Collections.sort(episodeList, new NaturalOrderComparator<>(true));
        Collections.reverse(episodeList);

        ArrayList<String> visibleTagsList = new ArrayList<>();
        ArrayList<ArrayList<String>> episodes = new ArrayList<>();

        if (hasExternalSubtitles)
            visibleTagsList.add(Strings.TAG_EXTERNAL_SUBTITLES);

        if(show.getTags() != null) {

            show.getTags().sort(String::compareToIgnoreCase);

            for (String tag : show.getTags()) {
                if (meetsTagCutoff(tag))
                    visibleTagsList.add(tag + " (" + tagList.get(tag.toLowerCase()) + ")");
            }
        }

        //creating the links for the episodes
        for (String episode : episodeList) {

            String path = episodesPathList.get(episode);
            String pathToShow = show.getPathToShow();

            if (path == null)
                path = "";

            String folderTag = path.replace(pathToShow, "")
                    .replace(episode, "")
                    .replace("/", "")
                    .replace("\\", "");

            if (folderTag.equals(""))
                folderTag = Strings.DEFAULT_FOLDER_NAME;

            ArrayList<String> tmpEpisode = new ArrayList<>();
            tmpEpisode.add(episode);
            tmpEpisode.add(ProjectPaths.getInstance().convertNetworkPathToLocal(path).replace("//", "/"));
            tmpEpisode.add(folderTag);
            episodes.add(tmpEpisode);

        }

        show.setVisibleTags(visibleTagsList);
        show.setEpisodes(episodes);
        show.setPathToShow(ProjectPaths.getInstance().convertNetworkPathToLocal(show.getPathToShow()).replace("//", "/"));

        Utils.saveJsonToFile(show, ProjectPaths.getInstance().getJsonShowFolder(), show.getTitle() + ".json");
    }

    private static void createRecommendation() {

        for (Object showEntry : showList.entrySet()) {

            Map.Entry pair = (Map.Entry) showEntry;
            ShowInfo currentShow = (ShowInfo) pair.getValue();

            if (currentShow.getRecommendations() == null || currentShow.getRecommendations().isEmpty())
                continue;

            for (ShowRecommendation recommendation : currentShow.getRecommendations()) {

                // checking if recommendation has already been created
                if (!recommendation.getMyTitle().equals(Strings.NULL))
                    continue;
                //checking to see if users has the show and using the users name instead of MALs
                if (alternativeTitleList.containsKey(recommendation.getTitle())) {
                    recommendation.setDownloaded(true);
                    recommendation.setMyTitle(alternativeTitleList.get(recommendation.getTitle()));
                }
            }
            Utils.saveJsonToFile(currentShow, ProjectPaths.getInstance().getJsonShowFolder(), currentShow.getTitle() + ".json");
        }
    }

    private static void createTags() {

        new File(ProjectPaths.getInstance().getJsonShowFolder()).mkdirs();
        try {
            new File(ProjectPaths.getInstance().getJsonShowFolder()).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File showList = new File(ProjectPaths.getInstance().getJsonShowFolder());

        ArrayList<String> tags = new ArrayList<>();

        for (File show : showList.listFiles()) {
            if (show.getName().contains(".json")) {
                ShowInfo showInfo = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonShowFolder(), show.getName(), ShowInfo.class);
                if(showInfo != null && showInfo.getTags() != null)
                    tags.addAll(showInfo.getTags());
            }
        }
        //getting number of times the tag is used
        for (String tag : tags) {
            tagList.merge(tag.toLowerCase(), 1, (a, b) -> a + b);
        }
    }


    private static boolean meetsTagCutoff(String tag) {
        return tagList.get(tag.toLowerCase()) != null && !tag.toLowerCase().equals("") && tagList.get(tag.toLowerCase()) > TAG_CUTOFF;
    }

    private static boolean isVideoFile(String title) {
        return !title.substring(0, 1).equals(".") && (title.contains(".mkv") || title.contains(".avi") || title.contains(".mp4") || title.contains(".mkv") || title.contains(".ogm"));
    }


    private static boolean isSubtitleFile(String title) {
        return !title.substring(0, 1).equals(".") && (title.contains(".ass") || title.contains(".srt"));
    }



}