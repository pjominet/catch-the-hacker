package tech.clusterfunk.game;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;
import tech.clusterfunk.game.characters.Hacker;
import tech.clusterfunk.game.characters.Player;
import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.game.network.Network;
import tech.clusterfunk.game.systems.OS;
import tech.clusterfunk.util.CommandLoader;
import tech.clusterfunk.util.exceptions.FatalException;
import tech.clusterfunk.util.exceptions.InvalidIPException;
import tech.clusterfunk.util.exceptions.UnknownIPException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static tech.clusterfunk.Main.CONFIG_ROOT;

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

    private void init(String playerName, String playerOs, String playerNick) {
        System.out.println(">> Setting up game...");

        Computer playerPC = new Computer(playerOs, playerNick, Integer.valueOf(initConfig.get("player_skill")));
        player = new Player(playerName, playerPC, Integer.valueOf(initConfig.get("player_skill")));

        Computer hackerPC = new Computer(initConfig.get("blackhat_os"),
                initConfig.get("blackhat_nick"),
                Integer.valueOf(initConfig.get("blackhat_diff"))
        );
        hacker = new Hacker(initConfig.get("blackhat_name"), hackerPC);

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

        // simulate command prompt at current FS position
        out.println(playerOS.getCurrentPath() + " > ls");
        // list child directories
        playerOS.list(playerOS.getCurrentFSPosition());

        // change permission
        playerOS.changeMode("+w", "Program Data", playerOS.getFsRoot(), 6);
        // list changes
        out.println(playerOS.getCurrentPath() +" > chmod +w Program Data");
        playerOS.list(playerOS.getCurrentFSPosition());
        // revert change permission
        playerOS.changeMode("-w", "Program Data", playerOS.getFsRoot(), 6);
        // list changes
        out.println(playerOS.getCurrentPath() +" > chmod -w Program Data");
        playerOS.list(playerOS.getCurrentFSPosition());
        System.out.println();

        // ping test
        System.out.print("Ping Computer: ");
        String ip = in.next();
        playerOS.ping(network, ip);

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
        playerOS.changeDirectory("~", playerOS.getCurrentFSPosition(), player.getSkill());
        System.out.print("\n" + playerOS.getCurrentPath() + " > ");
        String command = in.next();

        out.clear();
        return command;
    }
}
