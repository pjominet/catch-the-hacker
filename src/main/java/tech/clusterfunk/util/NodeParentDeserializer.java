package tech.clusterfunk.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.clusterfunk.game.systems.filesystem.Node;

import java.io.IOException;

public class NodeParentDeserializer extends JsonDeserializer {
    @Override
    public Node deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(jsonParser);
        return mapper.convertValue(
                node.findParent(node.get("path").asText()),
                Node.class);
    }
}
