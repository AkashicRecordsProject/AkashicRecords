package Java.models;

import com.google.gson.annotations.SerializedName;

public class ShowRecommendation {


    @SerializedName("officialTitle")
    private String title;

    @SerializedName("myTitle")
    private String myTitle;

    @SerializedName("link")
    private String link;

    @SerializedName("isDownloaded")
    private boolean isDownloaded;

    public ShowRecommendation(String title, String myTitle, String link, boolean isDownloaded) {
        this.title = title;
        this.myTitle = myTitle;
        this.link = link;
        this.isDownloaded = isDownloaded;
    }

    @Override
    public String toString() {
        return "Recommendation{" +
                "title='" + title + '\'' +
                ", myTitle='" + myTitle + '\'' +
                ", link='" + link + '\'' +
                ", isDownloaded=" + isDownloaded +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMyTitle() {
        return myTitle;
    }

    public void setMyTitle(String myTitle) {
        this.myTitle = myTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
