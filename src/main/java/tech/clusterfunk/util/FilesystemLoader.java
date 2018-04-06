package tech.clusterfunk.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.clusterfunk.game.systems.filesystem.Node;

import java.io.IOException;
import java.util.Iterator;

import static tech.clusterfunk.Main.CONFIG_ROOT;

public class FilesystemLoader {

    public static Node parseTree(String osName) throws IOException {
        String config = CONFIG_ROOT + osName + "_FS.json";
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(FilesystemLoader.class.getResourceAsStream(config));

        printTree(root);

        return mapper.convertValue(root, Node.class);
    }

    private static void printTree(JsonNode parentNode) {
        if (parentNode.isArray()) {

            Iterator<JsonNode> iter = parentNode.elements();

            while (iter.hasNext()) {
                JsonNode node = iter.next();

                if (node.isObject() || node.isArray()) {
                    printTree(node);
                } else {
                    System.out.print(node.asText());
                }
            }

        }
        if (parentNode.isObject()) {
            Iterator<String> iter = parentNode.fieldNames();

            while (iter.hasNext()) {
                String nodeName = iter.next();
                JsonNode node = parentNode.path(nodeName);

                if (nodeName.equals("path"))
                System.out.println(node.asText());

                if (node.isObject() || node.isArray()) {
                    printTree(node);
                }
            }
        }
    }
}
