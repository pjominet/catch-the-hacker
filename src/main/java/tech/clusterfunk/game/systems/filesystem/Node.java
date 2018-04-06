package tech.clusterfunk.game.systems.filesystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import tech.clusterfunk.util.NodeTypeDeserializer;

import java.util.LinkedList;
import java.util.List;

public class Node {
    private Node parent;
    private List<Node> children;
    private String name;
    private NodeType type;
    private String permissions;

    public Node() {
        children = new LinkedList<>();
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    @JsonProperty("type")
    @JsonDeserialize(using = NodeTypeDeserializer.class)
    public void setType(NodeType type) {
        this.type = type;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return name;
    }
}
