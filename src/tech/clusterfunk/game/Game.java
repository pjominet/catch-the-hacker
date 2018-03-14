package tech.clusterfunk.game;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.*;
import tech.clusterfunk.game.characters.Blackhat;
import tech.clusterfunk.game.characters.Player;
import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.game.network.Network;

public class Game {

    private Player player;
    private Blackhat blackhat;
    private Network network;
    private ColoredPrinter printer = new ColoredPrinter.Builder(1, false)
            .foreground(FColor.WHITE).background(BColor.BLACK).build();

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
        printer.println("=== Running Debug Mode ===\n", Attribute.BOLD, FColor.MAGENTA, BColor.BLACK);

        printer.println(printer + "\n", Attribute.NONE, FColor.WHITE, BColor.BLACK);

        printer.println("--- Player stats ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        printer.println(player.toString());
        printer.println(player.listCommands("DOORS"));
        printer.println(player.listCommands("LOONIX"));
        printer.println(player.listCommands("OSY"));
        printer.print("\n");
        printer.println("--- Hacker stats ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        printer.println(blackhat.toString());
        printer.print("\n");
        printer.println("--- Network map ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        printer.println(network.toString());

        printer.clear();
    }

    public void start() {
        printer.println("=== Catch The Hacker ===\n", Attribute.BOLD, FColor.GREEN, BColor.BLACK);

        printer.clear();
    }
}
