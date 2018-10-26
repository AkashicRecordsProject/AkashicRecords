package Java.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;


public class ShowContainer {


    @SerializedName("shows")
    private ArrayList<ShowInfo> shows;

    private String title = "";

    public ShowContainer(ArrayList<ShowInfo> shows) {
        this.shows = shows;
    }

    public ShowContainer() {
    }


    public ArrayList<ShowInfo> getShows() {
        return shows;
    }
    public void setShows(ArrayList<ShowInfo> shows) {
        this.shows = shows;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
