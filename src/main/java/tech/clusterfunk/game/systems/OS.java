package tech.clusterfunk.game.systems;

import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.util.FilesystemLoader;
import tech.clusterfunk.util.CommandLoader;

import java.io.IOException;
import java.util.List;

public class OS {
    private String name;
    private List<Command> commandSet;
    private Node fileSystemPosition;

    private void loadFS(String osName) {
        try {
            fileSystemPosition = FilesystemLoader.parseTree(osName);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public OS(String name) {
        this.name = name;
        commandSet = CommandLoader.loadCommandSet(name);
        loadFS(name);
    }

    public String getName() {
        return name;
    }

    public List<Command> getCommandSet() {
        return commandSet;
    }

    public Node getFileSystemPosition() {
        return fileSystemPosition;
    }

    public void setFileSystemPosition(Node fileSystemPosition) {
        this.fileSystemPosition = fileSystemPosition;
    }

    // TODO: review, should always print whole FS not part of it
    public void printFileSystem(Node current) {
        System.out.println(current.getPath());
        if (!current.getChildren().isEmpty()) {
            for (Node child : current.getChildren()) {
                printFileSystem(child);
            }
        }
    }

    public boolean hasCommand(Command input) {
        boolean found = false;
        for (Command command: commandSet) {
            found = input.equals(command);
            if (found) break;
        }
        return found;
    }

    // TODO: not working as intended
    public Node changeDirectory(String path, Node current) {
        if (!current.getPath().contains(path)) {
            if (!current.getChildren().isEmpty()) {
                for (Node child: current.getChildren()) {
                    current = changeDirectory(path, child);
                }
            }
        }
        return current;
    }
}
