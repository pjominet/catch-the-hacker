package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.game.systems.Command;
import tech.clusterfunk.util.IOHandler;

import java.util.List;

public class Player implements Hacker {
    private String name;
    private Computer computer;
    private List<Command> knowCommands;

    public Player(String name, Computer computer) {
        this.name = name;
        this.computer = computer;
        this.knowCommands = IOHandler.loadDefaultCommands();
    }

    public String listCommands(String os) {
        StringBuilder builder = new StringBuilder();
        builder.append(os).append(": ");
        for (Command cmd : knowCommands) {
            if (cmd.getOs().equals(os))
                builder.append(cmd.getName()).append(" ");
        }
        return builder.toString();
    }

    public void learnCommand(Command cmd) {
        knowCommands.add(cmd);
    }

    public void changeOS(String newOS) {
        computer.installNewOS(newOS);
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + ")";
    }

    @Override
    public void hack(Computer computer) {
        this.computer = computer;
    }
}
