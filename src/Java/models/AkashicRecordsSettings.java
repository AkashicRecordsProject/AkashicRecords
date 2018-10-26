package Java.models;


import Java.Strings;
import Java.tools.ProjectPaths;
import Java.tools.Utils;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class AkashicRecordsSettings {

    @SerializedName("rootNetworkPath")
    private String rootNetworkPath = Strings.NULL;

    @SerializedName("useNetwork")
    private boolean useNetwork = false;

    @SerializedName("useNetworkUser")
    private boolean useNetworkUser = false;

    @SerializedName("showFolderIncludesNetworkPaths")
    private boolean showFolderIncludesNetworkPaths = false;

    @SerializedName("showFolderPaths")
    private ArrayList<Path> showFolderPaths = new ArrayList<>();


    private static AkashicRecordsSettings ourInstance = new AkashicRecordsSettings();

    public static AkashicRecordsSettings getInstance() {
        return ourInstance;
    }

    private AkashicRecordsSettings() {
    }


    public void create(){

        ourInstance = Utils.getInstance().createObjectFromJson(ProjectPaths.getInstance().getJsonOtherFolder(), Strings.JSON_SETTINGS, AkashicRecordsSettings.class);

        if(ourInstance == null) {
            rootNetworkPath = ProjectPaths.getInstance().getRootFolder().replace("file://","");
            showFolderPaths.add(new Path());

            Utils.saveJsonToFile(this, ProjectPaths.getInstance().getJsonOtherFolder(), Strings.JSON_SETTINGS);
        }


    }

    public String getRootNetworkPath() {
        return rootNetworkPath;
    }

    public void setRootNetworkPath(String rootNetworkPath) {
        this.rootNetworkPath = rootNetworkPath;
    }

    public boolean isUseNetwork() {
        return useNetwork;
    }

    public void setUseNetwork(boolean useNetwork) {
        this.useNetwork = useNetwork;
    }

    public boolean isUseNetworkUser() {
        return useNetworkUser;
    }

    public void setUseNetworkUser(boolean useNetworkUser) {
        this.useNetworkUser = useNetworkUser;
    }

    public ArrayList<Path> getShowFolderPaths() {
        return showFolderPaths;
    }


    public boolean isShowFolderIncludesNetworkPaths() {
        return showFolderIncludesNetworkPaths;
    }

    public void setShowFolderIncludesNetworkPaths(boolean showFolderIncludesNetworkPaths) {
        this.showFolderIncludesNetworkPaths = showFolderIncludesNetworkPaths;
    }
}
