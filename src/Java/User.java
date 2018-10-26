package Java;

import Java.models.EpisodeTags;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;

public class User {

    private static User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    public enum THEME {
        DEFAULT("defaultTheme"),
        LIGHT("lightTheme"),
        NOISE("noiseTheme"),
        CUSTOM("defaultTheme");

        private String title;

        THEME(String title){
            this.title = title;
        }

        String getTitle(){
            return title;
        }
    }


    private static EpisodeTags episodeTags;

    @SerializedName("theme")
    private THEME theme = THEME.NOISE;
    @SerializedName("watching")
    private HashMap<String, String> watching = new HashMap<>();
    @SerializedName("showAiring")
    private boolean showAiring = true;
    @SerializedName("showOnlyWatchingAiring")
    private boolean showOnlyWatchingInAiring = false;

    private User() {
    }


    public void create() {

        ourInstance = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonUserFilePath(), Strings.JSON_USER,  getClass());
        episodeTags = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonUserFilePath(), Strings.JSON_EPISODE_TAGS, EpisodeTags.class);

        if(ourInstance == null)
            ourInstance = new User();

        if (episodeTags == null) {
            episodeTags = new EpisodeTags(new HashMap<>());
            Utils.saveJsonToFile(episodeTags, ProjectPaths.getInstance().getJsonUserFilePath(), Strings.JSON_EPISODE_TAGS);
        }

        saveJson();
    }

    public void toggleWatchingShow(String show){
        if(watching.containsKey(show))
            watching.remove(show);
        else
            watching.put(show,"");

        saveJson();
    }


    public void updateWatchingShow(String show, String selectedEpisode){
        watching.put(show, selectedEpisode);
        saveJson();
    }

    public void setTheme(THEME theme){
        this.theme = theme;
        saveJson();
    }

    public THEME getTheme(){
        return theme;
    }


    public int getNumberOfThemes(){
        return THEME.values().length;
    }


    public String getThemeName(boolean includeExtension) {

        if (includeExtension)
            return theme.title + ".css";

        return theme.title;
    }

    public String getWatchingSelectedEpisode(String show) {
        return watching.get(show);
    }

    public boolean isWatchingShow(String show){
        return watching.containsKey(show);
    }

    public int getWatchingListSize(){
        return watching.size();
    }



    private void saveJson(){
        Utils.saveJsonToFile(ourInstance, ProjectPaths.getInstance().getJsonUserFilePath(), Strings.JSON_USER);
    }

    public String getEpisodeTag(String episode){
        return episodeTags.getEpisodeTags().get(episode);
    }


    public boolean isShowAiring() {
        return showAiring;
    }

    public void setShowAiring(boolean showAiring) {
        this.showAiring = showAiring;
    }

    public boolean isShowOnlyWatchingInAiring() {
        return showOnlyWatchingInAiring;
    }

    public void setShowOnlyWatchingInAiring(boolean showOnlyWatchingInAiring) {
        this.showOnlyWatchingInAiring = showOnlyWatchingInAiring;
    }
}
