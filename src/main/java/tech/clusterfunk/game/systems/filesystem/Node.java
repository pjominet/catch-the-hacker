package tech.clusterfunk.game.systems.filesystem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import tech.clusterfunk.util.NodeTypeDeserializer;

import java.util.List;

public class Node {
    @JsonIgnore
    private Node parent;
    private List<Node> children;
    private String path;
    private NodeType type;
    @JsonIgnore
    private String permissions;

    public Node() {
        this.permissions = "r-w";
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
        StringBuilder builder = new StringBuilder();
        builder.append("path=").append(path)
                .append(", type=").append(type)
                .append(", permissions=").append(permissions);
        if (!children.isEmpty())
            builder.append("\nchildren=").append(children);
        if (parent != null)
            builder.append(", parent=").append(parent.getPath());
        return builder.toString();
    }
}
