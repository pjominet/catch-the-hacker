package tech.clusterfunk.game.systems.filesystem;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum NodeType {
    FILE("f"),
    DIRECTORY("d");

    private final String abbreviation;

    NodeType(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @JsonProperty("abbreviation")
    public String getAbbreviation() {
        return abbreviation;
    }

    public static NodeType fromAbbreviation(String type) {
        if (type != null) {
            for (NodeType nodeType: NodeType.values()) {
                if(type.equalsIgnoreCase(nodeType.abbreviation))
                    return nodeType;
            }
        }
        return null;
    }
}
