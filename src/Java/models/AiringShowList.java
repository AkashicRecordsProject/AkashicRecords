package Java.models;

import java.util.ArrayList;

public class AiringShowList {


    private String date;
    private ArrayList<String> showList = new ArrayList<>();


    public AiringShowList(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getShowList() {
        return showList;
    }

}
