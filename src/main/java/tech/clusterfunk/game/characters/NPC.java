package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.systems.network.Computer;
import tech.clusterfunk.util.ConfigLoader;

import java.util.Random;

public class NPC extends Character {
    int wallet;

    public NPC(int userID) {
        super(null, null);
        this.name = ConfigLoader.loadGenericUsername(userID);
        this.computer = new Computer(this.name);
        Random rnd = new Random(System.currentTimeMillis());
        this.wallet = rnd.nextInt(90) + 10;
    }

    NPC(String name, Computer computer, int wallet) {
        super(name, computer);
        this.wallet = wallet;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + "), WALLET: " + wallet;
    }
}
