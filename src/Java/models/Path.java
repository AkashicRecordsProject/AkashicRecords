package Java.models;

import Java.Strings;
import com.google.gson.annotations.SerializedName;

public class Path {

    @SerializedName("localFolder")
    private String localFolder = Strings.NULL;
    @SerializedName("linuxNetworkFolder")
    private String linuxNetworkFolder = Strings.NULL;
    @SerializedName("windowsNetworkFolder")
    private String windowsNetworkFolder = Strings.NULL;
    @SerializedName("macNetworkFolder")
    private String macNetworkFolder = Strings.NULL;

    public Path() {

    }

    public Path(String defaultFolder, String linuxNetworkFolder, String windowsNetworkFolder, String macNetworkFolder) {
        this.localFolder = defaultFolder;
        this.linuxNetworkFolder = linuxNetworkFolder;
        this.windowsNetworkFolder = windowsNetworkFolder;
        this.macNetworkFolder = macNetworkFolder;
    }

    public String getLocalFolder() {
        return localFolder;
    }

    public void setLocalFolder(String localFolder) {
        this.localFolder = localFolder;
    }

    public String getLinuxNetworkFolder() {
        return linuxNetworkFolder;
    }

    public void setLinuxNetworkFolder(String linuxNetworkFolder) {
        this.linuxNetworkFolder = linuxNetworkFolder;
    }

    public String getWindowsNetworkFolder() {
        return windowsNetworkFolder;
    }

    public void setWindowsNetworkFolder(String windowsNetworkFolder) {
        this.windowsNetworkFolder = windowsNetworkFolder;
    }

    public String getMacNetworkFolder() {
        return macNetworkFolder;
    }

    public void setMacNetworkFolder(String macNetworkFolder) {
        this.macNetworkFolder = macNetworkFolder;
    }
}
