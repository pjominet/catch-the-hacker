package tech.clusterfunk.util;

import org.json.JSONObject;
import org.json.JSONTokener;
import tech.clusterfunk.game.systems.filesystem.Node;
import tech.clusterfunk.game.systems.filesystem.NodeType;

import static tech.clusterfunk.Main.CONFIG_ROOT;

public class FilesystemLoader {

    public static Node parseFileSystem(String osName) {
        String config = CONFIG_ROOT + osName.toLowerCase() + "_fs.json";

        JSONTokener tokener = new JSONTokener(FilesystemLoader.class.getResourceAsStream(config));
        JSONObject root = new JSONObject(tokener);

        return buildTree(root);
    }

    private static Node buildTree(JSONObject current) {
        Node node = new Node();
        node.setName(current.getString("name"));
        node.setType(NodeType.fromAbbreviation(current.getString("type")));
        node.setPermissions(current.getString("permissions").toCharArray());

        current.getJSONArray("children").forEach(jsonChild -> {
            Node child = buildTree((JSONObject) jsonChild);
            node.getChildren().add(child);
            child.setParent(node);
        });
        return node;
    }
}
