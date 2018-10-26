package Java.tools;

import Java.models.AkashicRecordsSettings;
import Java.models.Path;


public class ProjectPaths {

    private static ProjectPaths ourInstance = new ProjectPaths();


    private static final String appRoot = "AkashicRecords/";

    //JAR file paths
    public static final String JAR_ROOT_FOLDER_PATH = "/";
    public static final String JAR_RESOURCE_FOLDER_PATH = JAR_ROOT_FOLDER_PATH + "resources/";
    public static final String JAR_CSS_FOLDER_PATH = JAR_RESOURCE_FOLDER_PATH + "css/";
    public static final String JAR_FONTS_FOLDER_PATH = JAR_RESOURCE_FOLDER_PATH + "fonts/";
    public static final String JAR_ICONS_FOLDER_PATH = JAR_RESOURCE_FOLDER_PATH + "icons/";
    public static final String JAR_IMAGES_FOLDER_PATH = JAR_RESOURCE_FOLDER_PATH + "images/";
    public static final String JAR_LAYOUTS_FOLDER_PATH = JAR_RESOURCE_FOLDER_PATH + "layouts/";

    //System folder paths
    private String rootFolder;
    private String resourcesFolder;
    private String jsonShowFolder;
    private String jsonContainersFolder;
    private String jsonOtherFolder;
    private String jsonUserFilePath;
    private String jsonFolder;
    private String imagesFolder;
    private String backgroundImagesFolder;
    private String thumbnailFolder;
    private String thumbnailLargeFolder;
    private String thumbnailSmallFolder;
    private String coverImagesFolder;
    private String headerFolder;
    private String htmlAnidb;
    private String htmlMal;



    public enum OS {
        LINUX,
        WINDOWS,
        MAC
    }

    private OS os;


    private ProjectPaths() {
    }

    public static ProjectPaths getInstance() {
        return ourInstance;
    }


    public static String createCleanPath(String path, String fileName) {
        path = path.replace("file://", "");
        return path + fileName;
    }


    public void createPaths() {

        String osName = System.getProperty("os.name").toLowerCase();


        if (osName.contains("windows"))
            os = OS.WINDOWS;
        else if (osName.contains("mac os"))
            os = OS.MAC;
        else
            os = OS.LINUX;


        AkashicRecordsSettings akashicRecordsSettings = AkashicRecordsSettings.getInstance();

        if (akashicRecordsSettings.isUseNetwork())
            rootFolder = "file://" + akashicRecordsSettings.getRootNetworkPath();
         else
            rootFolder = "file://" + System.getProperty("user.home") + "/" + appRoot;


        if (akashicRecordsSettings.isUseNetwork() && akashicRecordsSettings.isUseNetworkUser()) {
            if (os == OS.WINDOWS)
                jsonUserFilePath = rootFolder.replace("file://", "") + "JSON\\Other\\";
            else
                jsonUserFilePath = rootFolder + "JSON/Other/";

        } else {
            if (os == OS.WINDOWS)
                jsonUserFilePath = System.getProperty("user.home") + "\\" + appRoot.replace("/", "\\") + "JSON" + "\\" + "Other" + "\\";
            else
                jsonUserFilePath = System.getProperty("user.home").replace("file://", "") + "/" + appRoot + "JSON" + "/" + "Other" + "/";
        }


        resourcesFolder = rootFolder + "Resources" + "/";
        jsonFolder = rootFolder.replace("file://", "") + "JSON" + "/";

        if (os == OS.WINDOWS) {
            resourcesFolder = rootFolder.replace("file://", "") + "Resources" + "/";
            jsonFolder = rootFolder.replace("file://", "") + "JSON" + "/";
        } else if (os == OS.MAC) {
            resourcesFolder = rootFolder.replace("file://", "") + "Resources" + "/";
            jsonFolder = rootFolder.replace("file://", "") + "JSON" + "/";
        }


        jsonShowFolder = jsonFolder + "Shows" + "/";
        jsonContainersFolder = jsonFolder + "Containers" + "/";
        jsonOtherFolder = jsonFolder + "Other" + "/";
        imagesFolder = rootFolder + "Images" + "/";
        backgroundImagesFolder = imagesFolder + "Cover_Background" + "/";
        coverImagesFolder = imagesFolder + "Cover_Show" + "/";
        headerFolder = imagesFolder + "Headers" + "/";
        thumbnailFolder = imagesFolder + "Cover_Thumbnails" + "/";

        thumbnailLargeFolder = thumbnailFolder + "large" + "/";
        thumbnailSmallFolder = thumbnailFolder + "small" + "/";

        htmlAnidb = resourcesFolder.replace("file://", "") + "Html_aniDb" + "/";
        htmlMal = resourcesFolder.replace("file://", "") + "Html_MAL" + "/";

        if (os == OS.WINDOWS) {
            headerFolder = imagesFolder + "Headers" + "/";
            headerFolder = headerFolder.replace("file://", "").replace("\\", "/");
            backgroundImagesFolder = backgroundImagesFolder.replace("\\", "/");

            jsonFolder = jsonFolder.replace("\\", "/");
            jsonShowFolder = jsonFolder + "Shows\\";
            jsonContainersFolder = jsonFolder + "Containers\\";
        }


    }


    public String convertToNetWorkPath(String path) {

        if (!AkashicRecordsSettings.getInstance().isShowFolderIncludesNetworkPaths())
            return path;

        for (Path pathList : AkashicRecordsSettings.getInstance().getShowFolderPaths()) {

            if(path.contains(pathList.getLocalFolder().replace("\\","/"))) {

                System.out.println(path);
                System.out.println(pathList.getLocalFolder().replace("\\","/"));

                if (ProjectPaths.getInstance().getOS() == OS.LINUX)
                    return path.replace(pathList.getLocalFolder(), pathList.getLinuxNetworkFolder());
                else if (ProjectPaths.getInstance().getOS() == OS.WINDOWS)
                    return path.replace(pathList.getLocalFolder().replace("\\","/"), pathList.getWindowsNetworkFolder());
                else if (ProjectPaths.getInstance().getOS() == OS.MAC)
                    return path.replace(pathList.getLocalFolder(), pathList.getMacNetworkFolder());
            }

        }

        return path;
    }

    public String convertNetworkPathToLocal(String path) {

        if(!AkashicRecordsSettings.getInstance().isShowFolderIncludesNetworkPaths())
            return path.replace("\\", "/").replace("//", "/");


        for (Path pathList : AkashicRecordsSettings.getInstance().getShowFolderPaths()) {

            if (path.contains(pathList.getLinuxNetworkFolder()))
                return path.replace(pathList.getLinuxNetworkFolder(), pathList.getLocalFolder()).replace("\\", "/").replace("//", "/");
            else if (path.contains(pathList.getWindowsNetworkFolder()))
                return path.replace(pathList.getWindowsNetworkFolder(), pathList.getLocalFolder()).replace("\\", "/").replace("//", "/");
            else if (path.contains(pathList.getMacNetworkFolder()))
                return path.replace(pathList.getMacNetworkFolder(), pathList.getLocalFolder()).replace("\\", "/").replace("//", "/");
        }

        return path;

    }

    public OS getOS() {
        return os;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public String getResourcesFolder() {
        return resourcesFolder;
    }

    public String getJsonShowFolder() {
        return jsonShowFolder;
    }

    public String getJsonContainersFolder() {
        return jsonContainersFolder;
    }

    public String getJsonOtherFolder() {
        return jsonOtherFolder;
    }

    public String getJsonUserFilePath() {
        return jsonUserFilePath;
    }

    public String getJsonFolder() {
        return jsonFolder;
    }

    public String getImagesFolder() {
        return imagesFolder;
    }

    public String getBackgroundImagesFolder() {
        return backgroundImagesFolder;
    }

    public String getThumbnailFolder() {
        return thumbnailFolder;
    }

    public String getThumbnailLargeFolder() {
        return thumbnailLargeFolder;
    }

    public String getThumbnailSmallFolder() {
        return thumbnailSmallFolder;
    }

    public String getCoverImagesFolder() {
        return coverImagesFolder;
    }

    public String getHeaderFolder() {
        return headerFolder;
    }

    public String getHtmlAnidb() {
        return htmlAnidb;
    }

    public String getHtmlMal() {
        return htmlMal;
    }
}
