package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

public class Blackhat extends NPC {

    public Blackhat(String name, Computer computer) {
        super(name, computer, 0);
    }

    public void stealCoins(NPC npc, int amount) {
        this.wallet += amount;
        npc.setWallet(npc.getWallet() - amount);
    }
}
