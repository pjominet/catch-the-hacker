package tech.clusterfunk.game.systems.operatingsystem;

import java.util.List;
import java.util.Objects;

public class Command {
    private String name;
    private List<String> params;
    private String description;
    private String os;

    public Command(String name, List<String> params, String description, String os) {
        this.name = name;
        this.params = params;
        this.description = description;
        this.os = os;
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }

    public String getDescription() {
        return description;
    }

    public String getOS() {
        return os;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        if (params != null) {
            for (String param : params) {
                builder.append(" <").append(param).append(">");
            }
        }
        builder.append(": ").append(description);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Command)) return false;
        Command command = (Command) o;
        return Objects.equals(this.getName(), command.getName()) &&
                Objects.equals(this.getOS(), command.getOS());
    }
}
