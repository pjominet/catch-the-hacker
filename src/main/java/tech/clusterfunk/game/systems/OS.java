package tech.clusterfunk.game.systems;

import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.game.systems.filesystem.NodeType;
import tech.clusterfunk.util.CommandLoader;
import tech.clusterfunk.util.FilesystemLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OS {
    private String name;
    private Map<String, Command> commandSet;
    private Node fileSystemPosition;
    private String user;

    private void loadFS(String osName) {
        fileSystemPosition = FilesystemLoader.parseFileSystem(osName);
    }

    private void loadCS(String osName) {
        List<Command> set = CommandLoader.loadCommandSet(osName);
        commandSet = new ConcurrentHashMap<>();
        set.forEach(command -> commandSet.put(command.getName(), command));
    }

    public OS(String name, String user) {
        this.name = name;
        this.user = user;
        loadCS(this.name);
        loadFS(this.name);
        setCorrectUserHomeFolder(this.fileSystemPosition);
    }

    private void setCorrectUserHomeFolder(Node current) {
        if (current.getName().equals("NewUser")) current.setName(user);
        else current.getChildren().forEach(this::setCorrectUserHomeFolder);
    }

    private List<String> getDirectoriesToRoot() {
        Node current = fileSystemPosition;
        List<String> directoryNames = new ArrayList<>();
        while (current.getParent() != null) {
            directoryNames.add(current.getName());
            current = current.getParent();
        }
        directoryNames.add(current.getName());

        Collections.reverse(directoryNames);
        return directoryNames;
    }

    public String getName() {
        return name;
    }

    public Node getFileSystemPosition() {
        return fileSystemPosition;
    }

    public void setFileSystemPosition(Node fileSystemPosition) {
        this.fileSystemPosition = fileSystemPosition;
    }

    public String getUser() {
        return user;
    }

    public boolean hasCommand(String cmdName) {
        return commandSet.get(cmdName) != null;
    }

    public Command getFromCommandSet(String cmdName) {
        return commandSet.get(cmdName);
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

    public void listChildren(Node current) {
        current.getChildren().forEach(child ->
                System.out.format("%s %s " + child.getName() + "%n",
                        child.getType().getAbbreviation(),
                        child.getPermissions())
        );
        current.getChildren().forEach(this::listChildren);
    }

    public void changeDirectory(String directory, Node current) {
        if (current.getType() == NodeType.DIRECTORY) {
            if (directory.equals("..")) fileSystemPosition = current.getParent();
            else if (current.getName().equals(directory) ||
                    (directory.equals("~") && current.getName().equals(user)))
                fileSystemPosition = current;
            else current.getChildren().forEach(child -> changeDirectory(directory, child));
        }
    }
}
