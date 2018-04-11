package tech.clusterfunk.game;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;
import tech.clusterfunk.game.characters.Hacker;
import tech.clusterfunk.game.characters.Player;
import tech.clusterfunk.game.systems.network.Computer;
import tech.clusterfunk.game.systems.network.Network;
import tech.clusterfunk.game.systems.operatingsystem.OS;
import tech.clusterfunk.util.CommandLoader;
import tech.clusterfunk.util.exceptions.FatalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static tech.clusterfunk.Main.CONFIG_ROOT;
import static tech.clusterfunk.Main.SUDO;

public class Game {
    public static int DIFFICULTY = 0;
    private int turns;

    private Player player;
    private Hacker hacker;
    private Network network;

    private ColoredPrinter out;
    private Scanner in;
    private Map<String, String> initConfig;

    public Game() {
        out = new ColoredPrinter.Builder(1, false).build();
        in = new Scanner(System.in);
        loadInitConfig();
        this.turns = Integer.valueOf(initConfig.get("turns"));
    }

    private void loadInitConfig() {
        String config = CONFIG_ROOT + "init.config";
        initConfig = new ConcurrentHashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        CommandLoader.class.getResourceAsStream(config),
                        StandardCharsets.UTF_8
                )
        )) {
            for (String line; (line = reader.readLine()) != null; ) {
                String[] tokens = line.split("=");
                initConfig.put(tokens[0], tokens[1]);
            }
        } catch (IOException e) {
            System.err.println("No init config found at: " + config);
        }
    }

    private void init(String playerName, String playerOS, String playerUsername) {
        System.out.println(">> Setting up game...");

        int playerSkill = Integer.valueOf(initConfig.get("player_skill")) - DIFFICULTY;
        Computer playerPC = new Computer(playerOS, playerUsername, playerSkill);
        player = new Player(playerName, playerPC, playerSkill);

        int hackerSkill = Integer.valueOf(initConfig.get("hacker_skill")) + DIFFICULTY;
        Computer hackerPC = new Computer(initConfig.get("hacker_os"),
                initConfig.get("hacker_username"), hackerSkill);
        hacker = new Hacker(initConfig.get("hacker_name"), hackerPC);

        int networkSize = Integer.valueOf(initConfig.get("network_size")) * (DIFFICULTY + 1);
        network = new Network(networkSize);
        network.addComputer(hackerPC);
        network.addComputer(playerPC);

        /*
        System.out.print("\n>> Please select game difficulty (0=easy, 1=medium, 2=hard): ");
        DIFFICULTY = in.nextInt();
        */
        // for debugging automation
        DIFFICULTY = 0;

        System.out.println(">> Done!");
    }

    public void debug() {

        out.println("=== Running Debug Mode ===\n", Attribute.BOLD, FColor.MAGENTA, BColor.BLACK);

        out.println(out + "\n", Attribute.NONE, FColor.WHITE, BColor.BLACK);

        init("Patrick", "DOORS", "blyatrick");
        OS playerOS = player.getComputer().getOS();
        System.out.println();

        // debug stats
        /*
        out.println("--- Player stats ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        out.clear();
        out.println(player.toString());

        out.print("\n");
        out.println("--- Hacker stats ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        out.clear();
        out.println(hacker.toString());

        out.print("\n");
        out.println("--- Network map ---", Attribute.BOLD, FColor.CYAN, BColor.BLACK);
        out.clear();
        out.println(network.toString());
        */

        /* command simulation for debugging */
        // list
        out.println(playerOS.getCurrentPath() + " > ls");
        playerOS.list(SUDO);
        System.out.println();

        // change permission
        playerOS.changeMode("+w", "Program Data", playerOS.getFsRoot(), SUDO);
        out.println(playerOS.getCurrentPath() +" > chmod +w Program Data");
        playerOS.list(SUDO);
        System.out.println();

        //change directory
        out.println(playerOS.getCurrentPath() +" > cd Program Data");
        playerOS.changeDirectory("Program Data", playerOS.getFsRoot(), SUDO);

        // write to file
        out.println(playerOS.getCurrentPath() +" > echo Program Data");
        playerOS.writeToFile("This is some content", "test.txt", SUDO);
        playerOS.list(SUDO);
        System.out.println();

        //read from file
        out.println(playerOS.getCurrentPath() +" > vim test.txt");
        out.println(playerOS.readFromFile("test.txt", SUDO));
        System.out.println();

        // remove file
        out.println(playerOS.getCurrentPath() +" > rm test.txt");
        playerOS.remove("test.txt", SUDO);
        playerOS.list(SUDO);
        System.out.println();

        // ping test
        try {
            playerOS.ping(network,network.getRandomIP());
        } catch (FatalException e) {
            System.err.println(e.getMessage());
        }

        out.clear();
    }

    public void run() {
        out.println("=== Catch The Hacker ===\n", Attribute.BOLD, FColor.MAGENTA, BColor.BLACK);
        out.clear();

        String lastInput = newGame();
        out.println("Last given input: " + lastInput);

        in.close();
    }

    private String newGame() {
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
        OS playerOS = player.getComputer().getOS();

        out.print("\nAs installer finishes, you are being greeted by the welcome screen of " +
                playerOS.getName() + ".\n"
                + "You launch the terminal and start working...\n");
        // change to home directory
        playerOS.changeDirectory("~", playerOS.getFsRoot(), player.getSkill());
        System.out.print("\n" + playerOS.getCurrentPath() + " > ");
        String command = in.next();

        out.clear();
        return command;
    }
}
