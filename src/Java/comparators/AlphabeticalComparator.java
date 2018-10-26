package Java.comparators;

import Java.models.ShowInfo;
import javafx.scene.Node;
import java.util.Comparator;

public class AlphabeticalComparator implements Comparator<Node> {

    @Override
    public int compare(javafx.scene.Node nodeA, javafx.scene.Node nodeB) {

        try {
            ShowInfo showA = (ShowInfo) nodeA.getUserData();
            ShowInfo showB = (ShowInfo) nodeB.getUserData();
            return showA.getTitle().compareToIgnoreCase(showB.getTitle());
        } catch (Exception e){
            return 0;
        }
    }

}
