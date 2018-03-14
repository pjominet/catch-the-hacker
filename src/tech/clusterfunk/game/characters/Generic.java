package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

import java.util.Random;

public class Generic implements NPC {
    private String name;
    private Computer computer;
    private int awareness;

    Generic(String name, Computer computer) {
        this.name = name;
        this.computer = computer;
        Random rnd = new Random(System.currentTimeMillis());
        this.awareness = rnd.nextInt(5 - 1) + 1;
    }

    public String getName() {
        return name;
    }

    public Computer getComputer() {
        return computer;
    }

    public int getAwareness() {
        return awareness;
    }

    public void setAwareness(int awareness) {
        this.awareness = awareness;
    }

    @Override
    public String toString() {
        return "User: " + name + ", awareness: " + awareness;
    }
}
