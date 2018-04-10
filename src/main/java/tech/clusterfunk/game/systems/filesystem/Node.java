package tech.clusterfunk.game.systems.filesystem;

import java.util.LinkedList;
import java.util.List;

public class Node {
    private Node parent;
    private List<Node> children;
    private String name;
    private NodeType type;
    private char[] permissions;

    public Node() {
        children = new LinkedList<>();
        permissions = new char[3];
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

    public void setType(NodeType type) {
        this.type = type;
    }

    public char[] getPermissions() {
        return permissions;
    }

    public void setPermissions(char[] permissions) {
        this.permissions = permissions;
    }

    public boolean hasPermisson(char permission) {
        for (char c : permissions) {
            if (c == permission) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
