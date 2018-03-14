package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

public class Blackhat implements NPC, Hacker {
    private String name;
    private Computer computer;
    private int wallet;

    public Blackhat(String name, Computer computer) {
        this.name = name;
        this.computer = computer;
        this.wallet = 0;
    }

    public int getWallet() {
        return wallet;
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + "), WALLET=" + wallet;
    }

    @Override
    public void hack(Computer computer) {
        this.computer = computer;
    }
}
