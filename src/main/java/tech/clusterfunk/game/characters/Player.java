package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.systems.network.Computer;
import tech.clusterfunk.game.systems.operatingsystem.Command;
import tech.clusterfunk.util.ConfigLoader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Player extends Character {
    private Map<String, Command> knownCommands;
    private int skill;

    private void initDefaultCommands() {
        List<Command> defaults = ConfigLoader.loadDefaultCommands();
        knownCommands = new ConcurrentHashMap<>();
        defaults.forEach(command -> knownCommands.put(command.getName(), command));
    }

    public Player(String name, Computer computer, int skill) {
        super(name, computer);
        this.skill = skill;
        initDefaultCommands();
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public String listAvailableCommandsBy(String os) {
        StringBuilder builder = new StringBuilder();
        builder.append(os).append(": ");
        for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            if (entry.getValue().getOS().equals(os))
                builder.append(entry.getValue().getName()).append(" ");
        }
        return builder.toString();
    }

    public Map<String, Command> getAvailableCommands() {
        Map<String, Command> availableCommands = new ConcurrentHashMap<>();
        for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            if (entry.getValue().getOS().equals(this.computer.getOS().getName()))
                availableCommands.put(entry.getKey(), entry.getValue());
        }
        return availableCommands;
    }

    public void learnCommand(Command cmd) {
        knownCommands.put(cmd.getName(), cmd);
    }

    public void changeOS(String os, String user) {
        this.computer.installNewOS(os, user, skill);
    }
}
