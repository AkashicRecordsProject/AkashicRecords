package Java.tools;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.Node;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

public class Utils {


    private static Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }


    public static String capitalize(String string){
        return string.substring(0, 1).toUpperCase() + string.toLowerCase().substring(1);
    }

    public static String getDefaultShowTitle(String showTitle){
        return showTitle
                .replace(" ", "_")
                .replace("'","")
                .replace("<", "")
                .replace(">", "")
                .replace("\"", "")
                .replace("|", "")
                .replace("?", "")
                .replace(".", "_")
                .replace(":", "_")
                .replace("-", "_")
                .replace("\\", "_")
                .replace("/", "_")
                .replaceAll("\\P{Print}", "_")
                .toLowerCase();
    }

    public static String getCurrentSeason(boolean includeYear){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String season = "Fall";

        if(month <= 3)
            season = "Winter";
        if(month <= 6)
            season = "Spring";
        if(month <= 9)
            season = "Summer";

        if(includeYear)
            return season + " " + year;

        return season;

    }

    public <T> T createObjectFromJson(String jsonFolderPath, String fileName, Class<T> objectClassType){
        try {
            new File(jsonFolderPath).mkdirs();
            new File(jsonFolderPath).createNewFile();
            return new Gson().fromJson(new String(Files.readAllBytes(Paths.get(jsonFolderPath + fileName)), StandardCharsets.UTF_8), objectClassType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void saveJsonToFile(Object jsonObject, String folderLocation, String fileName) {

        folderLocation.replace("file://","");

        if (!fileName.contains(".json"))
            fileName = fileName + ".json";

        try {
            new File(folderLocation).mkdirs();
            new File(folderLocation).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(jsonObject);

        createFile(folderLocation, fileName, jsonOutput);
    }

    public static void setVisibility(Node node, boolean visible){
        if(visible){
            node.setVisible(true);
            node.setManaged(true);
        } else {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    public static void setVisibility(Node parentNode, Node node,boolean visible){
        if(visible){
            node.setVisible(true);
            parentNode.setManaged(true);
        } else {
            node.setVisible(false);
            parentNode.setManaged(false);
        }
    }

    public static void toggleVisibility(Node node){

        if(node.isVisible()){
            setVisibility(node,false);
        } else {
            setVisibility(node,true);
        }
    }

    public static boolean runningFromIntelliJ()
    {
        String classPath = System.getProperty("java.class.path");
        return classPath.contains("idea_rt.jar");
    }



    public String getResourceFileAsString(String resourceName){
        InputStream inputStream = getClass().getResourceAsStream("/resources/" + resourceName);
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    //if the you want the name to be the same as the resource name
    public void createFileFromResources(String location, String resourceName) {
        createFileFromResources(location, resourceName, resourceName);
    }

    public void createFileFromResources(String location, String name, String resourceName) {

        try {
            new File(location).mkdirs();
            new File(location).createNewFile();


            byte[] data = new byte[16384];
            int nRead;

            InputStream inputStream = getClass().getResourceAsStream("/resources/" + resourceName);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();


            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();

            FileOutputStream stream = new FileOutputStream(location + name);
            stream.write(buffer.toByteArray());
            stream.close();

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }


    public Image createImageFromURL(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void createImageFileFromURL(String URL, String saveLocation, String fileName){

        try {
            URL url = new URL(URL);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n;
            while (-1!=(n=in.read(buf)))
            {
                out.write(buf, 0, n);
            }

            out.close();
            in.close();
            byte[] response = out.toByteArray();
            FileOutputStream fos = new FileOutputStream(saveLocation.replace("file:", "") + fileName);
            fos.write(response);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void createImageFromResources(String location, String name, String resourcePath, String resourceName, String imageType) {
        try {
            location= location.replace("file://","");
            new File(location).mkdirs();
            new File(location).createNewFile();
            InputStream inputStream = getClass().getResourceAsStream(resourcePath + resourceName);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ImageIO.write(bufferedImage, imageType, new File(location + name));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }


    public static void createFile(String location, String name, String content){

        try {
            new File(location).mkdirs();
            new File(location).createNewFile();

            PrintWriter writer = new PrintWriter(location + name, "UTF-8");
            writer.print(content);
            writer.close();

        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    public void openWebPage(String location, String htmlName){
        new Thread(() -> {
            try {
                Desktop.getDesktop().open(new File(location + htmlName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void openLink(String link){
        new Thread(() -> {
            try {
                Desktop.getDesktop().browse(URI.create(link));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
