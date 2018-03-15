package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.game.systems.Command;
import tech.clusterfunk.util.IOHandler;

import java.util.List;

public class Player implements Hacker {
    private String name;
    private Computer computer;
    private List<Command> commandList;

    public Player(String name, Computer computer) {
        this.name = name;
        this.computer = computer;
        this.commandList = IOHandler.loadDefaultCommands();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Computer getComputer() {
        return computer;
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + ")";
    }

    @Override
    public void hack(Computer computer) {
        this.computer = computer;
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

    public void learnCommand(Command cmd) {
        commandList.add(cmd);
    }

    public void changeOS(String os) {
        this.computer.installNewOS(os);
    }
}
