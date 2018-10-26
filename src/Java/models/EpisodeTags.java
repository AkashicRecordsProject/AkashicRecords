package Java.models;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;

public class EpisodeTags {

    @SerializedName("episodeTags")
    private HashMap<String, String> episodeTags;


    public EpisodeTags(HashMap<String, String> episodeTags) {
        this.episodeTags = episodeTags;
    }

    public HashMap<String, String> getEpisodeTags() {
        return episodeTags;
    }

    public void setEpisodeTags(HashMap<String, String> episodeTags) {
        this.episodeTags = episodeTags;
    }



}
