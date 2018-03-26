package tech.clusterfunk.game.systems;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.util.IOHandler;

import java.io.IOException;
import java.util.List;

import static tech.clusterfunk.Main.CONFIG_ROOT;

public class OS {
    private String name;
    private List<Command> commandSet;
    private Node fileSystem;

    private Node loadFS() {
        String config = CONFIG_ROOT + name + "_FS.json";
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode;
        Node root = null;
        try {
            jsonNode = mapper.readTree(this.getClass().getResourceAsStream(config));
            root = mapper.convertValue(jsonNode, Node.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return root;
    }

    public OS(String name) {
        this.name = name;
        commandSet = IOHandler.loadCommandSet(name);
        this.fileSystem = loadFS();
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
