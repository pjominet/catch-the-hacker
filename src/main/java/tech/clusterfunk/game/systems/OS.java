package tech.clusterfunk.game.systems;

import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.util.FilesystemLoader;
import tech.clusterfunk.util.CommandLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OS {
    private String name;
    private List<Command> commandSet;
    private Node fileSystemPosition;

    private void loadFS(String osName) {
        fileSystemPosition = FilesystemLoader.parseFileSystem(osName);
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

    public boolean hasCommand(String cmdName) {
        boolean found = false;
        for (Command command : commandSet) {
            found = cmdName.equals(command.getName());
            if (found) break;
        }
        return found;
    }

    public List<String> getDirectoriesToRoot() {
        Node current = fileSystemPosition;
        List<String> pathNames = new ArrayList<>();
        while(current.getParent() != null) {
            pathNames.add(current.getName());
            current = current.getParent();
        }
        pathNames.add(current.getName());

        Collections.reverse(pathNames);
        return pathNames;
    }

    public String getCurrentPath() {
        StringBuilder builder = new StringBuilder();
        getDirectoriesToRoot().forEach(name -> {
            builder.append(name);
            if (!name.equals("/"))
                builder.append("/");
        });

        return builder.toString();
    }

    public void listDirectories(Node current) {
        current.getChildren().forEach(child -> System.out.println(child.getName()));
        current.getChildren().forEach(this::listDirectories);
    }

    public void changeDirectory(String dir, Node current) {
        if (current.getName().contains(dir)) fileSystemPosition = current;
        else current.getChildren().forEach(child -> changeDirectory(dir, child));
    }
}
