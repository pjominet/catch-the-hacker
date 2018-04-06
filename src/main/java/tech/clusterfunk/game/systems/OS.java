package tech.clusterfunk.game.systems;

import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.util.FilesystemLoader;
import tech.clusterfunk.util.CommandLoader;

import java.io.IOException;
import java.util.List;

public class OS {
    private String name;
    private List<Command> commandSet;
    private Node fileSystem;

    private void loadFS(String osName) {
        try {
            fileSystem = FilesystemLoader.parseTree(osName);
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

    public String showFS() {
        return fileSystem.toString();
    }

    public String getCurrentFSNode() {
        return fileSystem.getPathToCurrentNode();
    }

    public Command getCommand(String cmdName) {
        for (Command command : commandSet) {
            if (cmdName.equals(command.getName())) {
                return command;
            }
        }
        return null;
    }
}
