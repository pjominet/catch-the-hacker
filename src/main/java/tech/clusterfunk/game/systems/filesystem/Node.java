package tech.clusterfunk.game.systems.filesystem;

import java.util.LinkedList;
import java.util.List;

public class Node {
    private Node parent;
    private List<Node> children;
    private String name;
    private NodeType type;
    private char[] permissions;
    private String content;

    public Node() {
        children = new LinkedList<>();
        permissions = new char[3];
    }

    public Node(Node parent, String name, NodeType type, char[] permissions) {
        this.parent = parent;
        this.name = name;
        this.type = type;
        this.permissions = permissions;
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

    public Node findChildBy(String name) {
        Node found = null;
        for (Node child : children) {
            if (name.equals(child.name)) {
                found = child;
                break;
            }
        }
        return found;
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

    public void setType(NodeType type) {
        this.type = type;
    }

    public char[] getPermissions() {
        return permissions;
    }

    public void setPermissions(char[] permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermission(char permission) {
        for (char value : permissions) {
            if (value == permission) return true;
        }
        return false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return name;
    }
}
