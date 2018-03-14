package tech.clusterfunk.game.systems;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import tech.clusterfunk.game.systems.filesystem.NodeType;
import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.util.IOHandler;
import tech.clusterfunk.util.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class OS {
    private String name;
    private List<Command> commandSet;
    private Node filesystem;

    private void loadFS() {
        String config = "src/tech/clusterfunk/configs/" + name + "_FS.cnf";
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config))) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject json = new JSONObject(tokener);

            JSONObject filesystem = json.getJSONObject("filesystem");

            String path = filesystem.getString("path");

            String type = filesystem.getString("type");
            NodeType nodeType = NodeType.DIRECTORY;
            if(type.equals("f")) nodeType = NodeType.FILE;

            JSONArray children = filesystem.getJSONArray("children");
            List<Node> childNodes = null;
            if (children != null && children.length() > 0)
                childNodes = JsonUtils.toList(children);

            Node node = null;
            if (childNodes != null)
                node = new Node(null, childNodes, path, nodeType);

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
