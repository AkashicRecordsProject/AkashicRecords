package Java.models;

import Java.Strings;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class ShowInfo {


    @SerializedName("pathToShow")
    private String pathToShow = Strings.NULL;
    @SerializedName("title")
    private String title = Strings.NULL;
    @SerializedName("japaneseTitle")
    private String japaneseTitle = Strings.NULL;
    @SerializedName("malTitle")
    private String malTitle = Strings.NULL;
    @SerializedName("defaultTitle")
    private String defaultTitle = Strings.NULL;
    @SerializedName("hasExternalSubtitles")
    private boolean hasExternalSubtitles = false;
    @SerializedName("malLink")
    private String malLink = Strings.NULL;
    @SerializedName("aniDbLink")
    private String aniDbLink = Strings.NULL;
    @SerializedName("wikiLink")
    private String wikiLink = Strings.NULL;
    @SerializedName("studio")
    private String studio = Strings.NULL;
    @SerializedName("rating")
    private String rating = Strings.NULL;
    @SerializedName("ageRating")
    private String ageRating = Strings.NULL;
    @SerializedName("airDate")
    private String airDate = Strings.NULL;
    @SerializedName("plot")
    private String plot = Strings.NULL;
    @SerializedName("recommendations")
    private ArrayList<ShowRecommendation> recommendations = new ArrayList<>();
    @SerializedName("tags")
    private ArrayList<String> tags = new ArrayList<>();
    @SerializedName("visibleTags")
    private ArrayList<String> visibleTags = new ArrayList<>();
    @SerializedName("episodes")
    private ArrayList<ArrayList<String>> episodes = new ArrayList<>();

    public static final int FILE_NAME = 0;
    public static final int FULL_PATH = 1;
    public static final int FOLDER_NAME = 2;

    public ShowInfo(){}

    @Override
    public String toString() {
        return "ShowInfo{" +
                "pathToShow='" + pathToShow + '\'' +
                "\ntitle='" + title + '\'' +
                "\nmalTitle=" + malTitle +
                "\nhasExternalSubtitles=" + hasExternalSubtitles +
                "\nmalLink='" + malLink + '\'' +
                "\naniDbLink='" + aniDbLink + '\'' +
                "\nwikiLink='" + wikiLink + '\'' +
                "\nstudio='" + studio + '\'' +
                "\nrating='" + rating + '\'' +
                "\nageRating='" + ageRating + '\'' +
                "\nairDate='" + airDate + '\'' +
                "\nplot='" + plot + '\'' +
                "\nrecommendations=" + recommendations +
                "\ntags=" + tags +
                "\nvisibleTags=" + visibleTags +
                "\nepisodes=" + episodes +
                '}';
    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJapaneseTitle() {
        return japaneseTitle;
    }

    public void setJapaneseTitle(String japaneseTitle) {
        this.japaneseTitle = japaneseTitle;
    }

    public String getMalTitle() {
        return malTitle;
    }

    public void setMalTitle(String malTitle) {
        this.malTitle = malTitle;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getAniDbLink() {
        return aniDbLink;
    }

    public void setAniDbLink(String aniDbLink) {
        this.aniDbLink = aniDbLink;
    }

    public String getPathToShow() {
        return pathToShow;
    }

    public void setPathToShow(String pathToShow) {
        this.pathToShow = pathToShow;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public void setVisibleTags(ArrayList<String> visibleTags) {
        this.visibleTags = visibleTags;
    }

    public ArrayList<String> getVisibleTags() {
        return visibleTags;
    }

    public ArrayList<ArrayList<String>> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(ArrayList<ArrayList<String>> episodes) {
        this.episodes = episodes;
    }

    public boolean isHasExternalSubtitles() {
        return hasExternalSubtitles;
    }

    public void setHasExternalSubtitles(boolean hasExternalSubtitles) {
        this.hasExternalSubtitles = hasExternalSubtitles;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public String getMalLink() {
        return malLink;
    }

    public void setMalLink(String malLink) {
        this.malLink = malLink;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public ArrayList<ShowRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(ArrayList<ShowRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
