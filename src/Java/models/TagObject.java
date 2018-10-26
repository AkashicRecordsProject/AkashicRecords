package Java.models;

import javafx.scene.Node;

public class TagObject {

    private String tag;
    private Node node;


    public TagObject(){}

    public TagObject(String tag, Node node) {
        this.tag = tag;
        this.node = node;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }







}
