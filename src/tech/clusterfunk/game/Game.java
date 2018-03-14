package tech.clusterfunk.game;

import tech.clusterfunk.game.characters.Blackhat;
import tech.clusterfunk.game.characters.Player;
import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.game.network.Network;

public class Game {

    private Player player;
    private Blackhat blackhat;
    private Network network;

    public Game() {
        System.out.println("Setting up game:");

        System.out.println("Loading Player...");
        Computer playerPC = new Computer("DOORS", 1, "blyatrick");
        player = new Player("Patrick", playerPC);

        System.out.println("Loading Hacker...");
        Computer blackhatPC = new Computer("LOONIX", 5, "h4ck3rm4n");
        blackhat = new Blackhat("Hackerman", blackhatPC);

        System.out.println("Loading network...");
        network = new Network(10);
        network.addComputer(playerPC);
        network.addComputer(blackhatPC);

        System.out.print("Done!\n\n");
    }

    public void debug() {
        System.out.println("--- Player stats ---");
        System.out.println(player.toString());
        System.out.println(player.listCommands("DOORS"));
        System.out.println(player.listCommands("LOONIX"));
        System.out.println(player.listCommands("OSY"));
        System.out.println();
        System.out.println("--- Hacker stats ---");
        System.out.println(blackhat.toString());
        System.out.println();
        System.out.println("--- Network map ---");
        System.out.println(network.toString());
    }

    public void start() {

    }
}
