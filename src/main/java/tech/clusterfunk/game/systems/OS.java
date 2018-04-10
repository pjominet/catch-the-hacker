package tech.clusterfunk.game.systems;

import tech.clusterfunk.game.network.Network;
import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.game.systems.filesystem.NodeType;
import tech.clusterfunk.util.CommandLoader;
import tech.clusterfunk.util.FilesystemLoader;
import tech.clusterfunk.util.exceptions.InvalidIPException;
import tech.clusterfunk.util.exceptions.UnknownIPException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static tech.clusterfunk.Main.SUDO;

public class OS {
    private String name;
    private Map<String, Command> commandSet;
    private Node fileSystemPosition;
    private String user;
    private int accessLevel;

    private void loadFS(String osName) {
        fileSystemPosition = FilesystemLoader.parseFileSystem(osName);
    }

    private void loadCS(String osName) {
        List<Command> set = CommandLoader.loadCommandSet(osName);
        commandSet = new ConcurrentHashMap<>();
        set.forEach(command -> commandSet.put(command.getName(), command));
    }

    public OS(String name, String user, int accessLevel) {
        this.name = name;
        this.user = user;
        this.accessLevel = accessLevel;
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

    private boolean isPermitted(Node node, String permission, int accessLevel) {
        // sudo case - can skip all other checks
        if (accessLevel == SUDO) return true;
        // if access level is not sudo
        else if (accessLevel >= this.accessLevel) {
            if(node.getPermissions().contains(permission)) return true;
            else {
                System.err.println("Permission denied.");
                return false;
            }
        } else {
            System.err.println("Not high enough privilege.");
            return false;
        }

    }

    public void changeDirectory(String directory, Node current, int accessLevel) {
        if (current.getType() == NodeType.DIRECTORY) {
            if (isPermitted(current, "r", accessLevel)) {
                if (directory.equals("..")) fileSystemPosition = current.getParent();
                else if (current.getName().equals(directory) ||
                        (directory.equals("~") && current.getName().equals(user)))
                    fileSystemPosition = current;
                else current.getChildren().forEach(child -> changeDirectory(directory, child, accessLevel));
            }
        } else System.err.println("Destination is no directory.");
    }

    public void ping(Network network, String ip) {
        if (ip.equals("127.0.0.1"))
            System.err.println("Pinging your own system is somewhat pointless, don't you think?");
        else {
            try {
                if (network.isComputerAt(ip))
                    System.out.println("Found active computer at " + ip);
            } catch (InvalidIPException | UnknownIPException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void list(Node current) {
        current.getChildren().forEach(child ->
                System.out.format("%s %s " + child.getName() + "%n",
                        child.getType().getAbbreviation(),
                        child.getPermissions())
        );
    }
}
