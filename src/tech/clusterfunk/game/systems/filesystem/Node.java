package tech.clusterfunk.game.systems.filesystem;

public class Node {
    private String path;
    private FileType type;
    private String permissions;

    public Node(String path, FileType type) {
        this.path = path;
        this.type = type;
        this.permissions = "r-w";
    }

    public String getPath() {
        return path;
    }

    public FileType getType() {
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
        return "Node{path=" + path + ", type=" + type + ", permissions=" + permissions +"}";
    }
}
