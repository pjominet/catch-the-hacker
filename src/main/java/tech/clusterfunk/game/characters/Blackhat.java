package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

public class Blackhat extends NPC implements Hacker {
    private int wallet;

    public Blackhat(String name, Computer computer) {
        super(name, computer, 3);
        this.wallet = 0;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    @Override
    public void hack(Computer computer) {
        this.computer = computer;
    }

    @Override
    public String toString() {
        return super.toString() + ", WALLET=" + wallet;
    }
}
