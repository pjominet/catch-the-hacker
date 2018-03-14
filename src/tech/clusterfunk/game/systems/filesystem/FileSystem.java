package tech.clusterfunk.game.systems.filesystem;


import java.util.List;

public class FileSystem {
    private Node node;
    private List<Node> children;

    public FileSystem(Node node, List<Node> children) {
        this.node = node;
        this.children = children;
    }

    public Node getNode() {
        return node;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void changeDirectory(String path) {
        for (Node node: children) {
            if (node.getPath().equals(path)) {
                this.node = node;
                break;
            }
        }
    }
}
