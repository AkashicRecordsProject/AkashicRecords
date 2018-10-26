package Java;

import Java.models.AkashicRecordsSettings;
import Java.models.ShowInfo;
import Java.tools.ProjectPaths;
import org.imgscalr.Scalr;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Thumbnail {

    public enum  THUMBNAIL_SIZE {

        SMALL(167,215),
        LARGE(310,402);

        private int width;
        private int height;

        THUMBNAIL_SIZE(int width, int height){
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

    public Thumbnail() {
    }


    public static BufferedImage createThumbnail(String imagePath, ShowInfo showInfo, THUMBNAIL_SIZE thumbnailSize, boolean overwriteOldThumbnail){
        return createThumbnailBase(imagePath, showInfo.getDefaultTitle(), thumbnailSize, overwriteOldThumbnail);
    }

    public static BufferedImage createThumbnail(String imagePath, String fileName, THUMBNAIL_SIZE thumbnailSize, boolean overwriteOldThumbnail){
        return createThumbnailBase(imagePath, fileName, thumbnailSize, overwriteOldThumbnail);
    }

    private static BufferedImage createThumbnailBase(String imagePath, String fileName, THUMBNAIL_SIZE thumbnailSize, boolean overwriteOldThumbnail) {

        String thumbnailMod = "_" + thumbnailSize.width + "_" + thumbnailSize.height;

        String pathToThumbnail = ProjectPaths.createCleanPath(ProjectPaths.getInstance().getThumbnailFolder(), fileName + thumbnailMod + ".jpg");
        String pathToImage = ProjectPaths.createCleanPath(imagePath, fileName + ".jpg");

        //checking to see if thumbnail exists
        if (new File(pathToThumbnail).exists() && !overwriteOldThumbnail) {
            try {
                return ImageIO.read(new URL("file://" + pathToThumbnail));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //creating thumbnail for cover if it has an image
        if (new File(pathToImage).exists()) {
            try {
                new File(ProjectPaths.getInstance().getThumbnailFolder().replace("file://", "")).mkdirs();
                new File(ProjectPaths.getInstance().getThumbnailFolder().replace("file://", "")).createNewFile();

                BufferedImage bufferedImage;
                if (ProjectPaths.getInstance().getOS() == ProjectPaths.OS.WINDOWS && !AkashicRecordsSettings.getInstance().isUseNetwork())
                    bufferedImage = ImageIO.read(new File(pathToImage.replace("/", "\\")));
                else
                    bufferedImage = ImageIO.read(new URL("file://" + pathToImage));

                bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, thumbnailSize.getWidth(), thumbnailSize.getHeight());
                ImageIO.write(bufferedImage, "PNG", new File(pathToThumbnail));
                return bufferedImage;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;

    }




}
