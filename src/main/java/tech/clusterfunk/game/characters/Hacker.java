package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.systems.network.Computer;

public class Hacker extends NPC {

    public Hacker(String name, Computer computer) {
        super(name, computer, 0);
    }

    public void stealCoins(NPC npc, int amount) {
        this.wallet += amount;
        npc.setWallet(npc.getWallet() - amount);
    }
}
