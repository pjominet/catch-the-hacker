package tech.clusterfunk.game.systems;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import tech.clusterfunk.game.systems.filesystem.FileSystem;
import tech.clusterfunk.game.systems.filesystem.FileType;
import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.util.IOHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class OS {
    private String name;
    private FileSystem fileSystem;
    private List<Command> commandSet;

    private void loadFS() {
        String config = "src/tech/clusterfunk/configs/" + name + "_FS.cnf";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config))) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject json = new JSONObject(tokener);
            JSONObject filesystem = json.getJSONObject("filesystem");
            String path = filesystem.getString("path");
            String type = filesystem.getString("type");

            FileType fileType = FileType.DIRECTORY;
            if(type.equals("f")) fileType = FileType.FILE;
            Node node = new Node(path, fileType);

            JSONArray children = filesystem.getJSONArray("children");

            List<Node> childNodes = new ArrayList<>();

            fileSystem = new FileSystem(node, childNodes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OS(String name) {
        this.name = name;
        commandSet = IOHandler.loadCommandSet(this.name);
    }

    public String getName() {
        return name;
    }

    public List<Command> getCommandSet() {
        return commandSet;
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
