package tech.clusterfunk.game.systems;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.util.IOHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static tech.clusterfunk.Main.CONFIG_ROOT;

public class OS {
    private String name;
    private List<Command> commandSet;
    private Node fileSystem;

    private Node loadFS() {
        String config = CONFIG_ROOT + name + "_FS.json";
        ObjectMapper objectMapper = new ObjectMapper();

        Node node = null;
        try {
            node = objectMapper.readValue(new File(config), Node.class);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return node;
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

    public Command getCommand(String cmdName) {
        Command command = null;
        for (Command cmd : commandSet) {
            if (cmdName.equals(cmd.getName())) {
                command = cmd;
                break;
            }
        }
        return command;
    }
}
