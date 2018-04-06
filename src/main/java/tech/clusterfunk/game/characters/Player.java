package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.game.systems.Command;
import tech.clusterfunk.game.systems.OS;
import tech.clusterfunk.util.CommandLoader;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character implements Hacker {
    private List<Command> commandList;

    public Player(String name, Computer computer) {
        super(name, computer);
        this.commandList = CommandLoader.loadDefaultCommands();
    }

    public String listCommands(String os) {
        StringBuilder builder = new StringBuilder();
        builder.append(os).append(": ");
        for (Command cmd : commandList) {
            if (cmd.getOs().equals(os))
                builder.append(cmd.getName()).append(" ");
        }
        return builder.toString();
    }

    public List<Command> getAvailableCommands() {
        List<Command> availableCommands = new ArrayList<>();
        for (Command command: commandList) {
            if (command.getOs().equals(this.computer.getOs().getName()))
                availableCommands.add(command);
        }
        return availableCommands;
    }

    // TODO: add custom exception when command is null instead of returning null
    public Command useCommand(String cmdName) {
        Command found = null;
        for (Command command: getAvailableCommands()) {
            if (cmdName.equals(command.getName()))
                found = command;
            if (found != null) break;
        }
        return found;
    }

    public void learnCommand(Command cmd) {
        commandList.add(cmd);
    }

    @Override
    public void hack(Computer computer) {
        this.computer = computer;
    }

    public void changeOS(String os) {
        this.computer.installNewOS(os);
    }
}
