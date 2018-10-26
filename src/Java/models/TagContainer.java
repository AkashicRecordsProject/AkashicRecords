package Java.models;

import Java.Strings;
import java.util.ArrayList;

public class TagContainer {

    public interface onCompletedListener {
        void onCompleted();
    }

    private ArrayList<TagObject> tags = new ArrayList<>();
    private String selectedTag = "";

    public TagContainer() { }

    public void setAllTagVisibility(boolean visible) {

        for (TagObject object : tags) {
            object.getNode().setVisible(visible);
            object.getNode().setManaged(visible);
        }
    }

    public void searchForTagsWithTitle(String search) {

        for (TagObject tag : tags) {

            if (tag.getTag().split(Strings.TAG_SEPARATOR)[0].toLowerCase().contains(search.toLowerCase())) {
                tag.getNode().setVisible(true);
                tag.getNode().setManaged(true);
            } else {
                tag.getNode().setVisible(false);
                tag.getNode().setManaged(false);
            }


        }
    }

    public void selectTag(String tag, onCompletedListener completedListener) {
        selectTag(tag);

        tags.get(0).getNode().visibleProperty().addListener((observableValue, aBoolean, t1) ->
                completedListener.onCompleted());

    }


    private void alternateBackgroundColor(TagObject object, boolean dark) {

        if (object.getNode().getStyleClass().contains(Strings.CLASS_EPISODE_CONTAINER_LIGHT) ||
                object.getNode().getStyleClass().contains(Strings.CLASS_EPISODE_CONTAINER_DARK)) {

            if (dark) {
                object.getNode().getStyleClass().remove(Strings.CLASS_EPISODE_CONTAINER_LIGHT);
                object.getNode().getStyleClass().remove(Strings.CLASS_EPISODE_CONTAINER_DARK);
                object.getNode().getStyleClass().add(Strings.CLASS_EPISODE_CONTAINER_DARK);
            } else {
                object.getNode().getStyleClass().remove(Strings.CLASS_EPISODE_CONTAINER_DARK);
                object.getNode().getStyleClass().remove(Strings.CLASS_EPISODE_CONTAINER_LIGHT);
                object.getNode().getStyleClass().add(Strings.CLASS_EPISODE_CONTAINER_LIGHT);
            }
        }
    }

    public void selectTag(String tag) {


        if (selectedTag.equals(tag)) {
            boolean dark = false;
            for (TagObject object : tags) {
                alternateBackgroundColor(object, dark);
                dark = !dark;
                object.getNode().setVisible(true);
                object.getNode().setManaged(true);
            }

            selectedTag = "";
        } else {

            boolean dark = false;
            for (TagObject object : tags) {
                for (String splitTag : object.getTag().split(Strings.TAG_SEPARATOR)) {
                    if (tag.equals(splitTag)) {
                        alternateBackgroundColor(object, dark);
                        dark = !dark;
                        object.getNode().setVisible(true);
                        object.getNode().setManaged(true);
                        break;
                    } else {
                        object.getNode().setVisible(false);
                        object.getNode().setManaged(false);
                    }
                }

            }
            selectedTag = tag;
        }

    }

    public ArrayList<TagObject> getTags() {
        return tags;
    }

}
