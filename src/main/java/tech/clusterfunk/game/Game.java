package tech.clusterfunk.game;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.*;
import tech.clusterfunk.game.characters.Blackhat;
import tech.clusterfunk.game.characters.Player;
import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.game.network.Network;

import java.util.Scanner;

public class Game {

    private Player player;
    private Blackhat blackhat;
    private Network network;

    private ColoredPrinter out;
    private Scanner in;

    public Game() {
        out = new ColoredPrinter.Builder(1, false).build();
        in = new Scanner(System.in);
    }

    private void init(String playerName, String playerOs, String playerNick) {
        System.out.println(">> Setting up system...");

        Computer blackhatPC = new Computer("LOONIX", 5, "h4ck3rm4n");
        blackhat = new Blackhat("Hackerman", blackhatPC);

        Computer playerPC = new Computer(playerOs, 1, playerNick);
        player = new Player(playerName, playerPC);

        network = new Network(10);
        network.addComputer(blackhatPC);
        network.addComputer(playerPC);

        System.out.println(">> Done!");
    }

    public void debug() {

        out.println("=== Running Debug Mode ===\n", Attribute.BOLD, FColor.MAGENTA, BColor.BLACK);

        out.println(out + "\n", Attribute.NONE, FColor.WHITE, BColor.BLACK);

        init("Patrick", "DOORS", "blyatrick");
        System.out.println();

        out.println("--- Player stats ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        out.clear();
        out.println(player.toString());
        out.println(player.listCommands("DOORS"));
        out.println(player.listCommands("LOONIX"));
        out.println(player.listCommands("OSY"));
        out.println(player.getComputer().getOs().showFS());
        out.print("\n");
        out.println("--- Hacker stats ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        out.clear();
        out.println(blackhat.toString());
        out.println(blackhat.getComputer().getOs().showFS());
        out.print("\n");
        out.println("--- Network map ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        out.clear();
        out.println(network.toString());

        System.out.print("Find Computer: ");
        String ip = in.next();
        Computer computer = network.findComputerAt(ip);
        if (computer != null)
            out.println(computer.toString());
        else out.println("No computer found at this address!");

        out.clear();
    }

    public void run() {
        out.println("=== Catch The Hacker ===\n", Attribute.BOLD, FColor.MAGENTA, BColor.BLACK);
        out.clear();

        String lastCmd = start();
        out.println("Last given command: " + lastCmd);
        in.close();
    }

    private String start() {
        out.print("It is your first day as cybersecurity employee at CySec.\n" +
                "You walk to your office, take a seat at your new desk and boot your company computer.\n" +
                "The screen flashes briefly and a prompt appears...\n");
        System.out.print("\n>> Please enter your name: ");
        String name = in.nextLine();

        out.println("\nAs the system processes your name a new prompt appears...");
        System.out.print("\n>> Welcome to CySec " + name + "!\n" +
                ">> Please choose an Operation System: ");
        String os = in.next();

        out.println("\nThe system installer begins to run and it prompts you again...");
        System.out.print("\n>> Please provide a username: ");
        String nick = in.next();

        init(name, os, nick);

        out.print("\nAs installer finishes, you are being greeted by the welcome screen of " +
                player.getComputer().getOs().getName() + ".\n"
                + "You launch the terminal and start working...\n");
        System.out.print("\n" + player.getComputer().getOs().getCurrentFSNode() + " > ");
        String command = in.next();

        out.clear();
        return command;
    }
}
