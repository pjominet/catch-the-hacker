package tech.clusterfunk.game.systems.filesystem;

import java.util.List;

public class Node {
    private Node parent;
    private List<Node> children;
    private String path;
    private NodeType type;
    private String permissions;

    public Node(Node parent, List<Node> children, String path, NodeType type) {
        this.parent = parent;
        this.children = children;
        this.path = path;
        this.type = type;
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

    public String getPath() {
        return path;
    }

    public NodeType getType() {
        return type;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Node{" +
                "parent=" + parent +
                ", children=" + children +
                ", path='" + path +
                ", type=" + type +
                ", permissions='" + permissions +
                '}';
    }
}
