package tech.clusterfunk.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import tech.clusterfunk.game.systems.filesystem.NodeType;

import java.io.IOException;

public class NodeTypeDeserializer extends JsonDeserializer {
    @Override
    public NodeType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        NodeType type = NodeType.fromAbbreviation(jsonParser.getValueAsString());
        if (type != null) {
            return type;
        }
        throw new JsonMappingException("Invalid value for type: Must be 'd' or 'f'");
    }
}
