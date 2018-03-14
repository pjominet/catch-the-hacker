package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

public class Blackhat implements NPC, Hacker {
    private String name;
    private Computer computer;
    private int wallet;
    private int awareness;

    public Blackhat(String name, Computer computer) {
        this.name = name;
        this.computer = computer;
        this.wallet = 0;
        this.awareness = 3;
    }

    public int getWallet() {
        return wallet;
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
    public int getAwareness() {
        return awareness;
    }

    @Override
    public void setAwareness(int awareness) {
        this.awareness = awareness;
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + "), WALLET=" + wallet + ", AWARENESS=" + awareness;
    }

    @Override
    public void hack(Computer computer) {
        this.computer = computer;
    }
}
