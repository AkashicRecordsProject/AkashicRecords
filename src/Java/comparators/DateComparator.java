package Java.comparators;

import Java.models.ShowInfo;
import javafx.scene.Node;
import java.util.Comparator;


public class DateComparator implements Comparator<Node> {


    @Override
    public int compare(Node nodeA, Node nodeB) {

        try {
            ShowInfo showA = (ShowInfo) nodeA.getUserData();
            ShowInfo showB = (ShowInfo) nodeB.getUserData();

            return Integer.compare(createNewDate(showB.getAirDate()), createNewDate(showA.getAirDate()));
        } catch (Exception e){
            return 0;
        }

    }


    private int createNewDate(String date){
        String seasonConvert = "0";

        if(date.split(" ")[0].contains("Spring")){
            seasonConvert = "1";
        } else if(date.split(" ")[0].contains("Summer")){
            seasonConvert = "2";
        } else if(date.split(" ")[0].contains("Fall")){
            seasonConvert = "3";
        }

        return Integer.valueOf(date.split(" ")[1] + seasonConvert);
    }

}
