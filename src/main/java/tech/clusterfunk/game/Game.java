package tech.clusterfunk.game;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;
import tech.clusterfunk.game.characters.Hacker;
import tech.clusterfunk.game.characters.Player;
import tech.clusterfunk.game.systems.network.Computer;
import tech.clusterfunk.game.systems.network.Network;
import tech.clusterfunk.game.systems.operatingsystem.Command;
import tech.clusterfunk.game.systems.operatingsystem.OS;
import tech.clusterfunk.util.CommandLoader;
import tech.clusterfunk.util.exceptions.FatalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        out.println(playerOS.getCurrentPath() + " > dir");
        execute("dir", playerOS, SUDO);
        System.out.println();

        // change permission
        out.println(playerOS.getCurrentPath() + " > mode +w \"Program Data\"");
        execute("mode +w \"Program Data\"", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        System.out.println();

        //change directory
        out.println(playerOS.getCurrentPath() + " > chdir \"Program Data\"");
        execute("chdir \"Program Data\"", playerOS, SUDO);

        // write to file
        out.println(playerOS.getCurrentPath() + " > echo \"This is some content\" test.txt");
        execute("echo \"This is some content\" test.txt", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        System.out.println();

        //read from file
        out.println(playerOS.getCurrentPath() + " > note test.txt");
        execute("note test.txt", playerOS, SUDO);
        System.out.println();

        // copy test
        out.println(playerOS.getCurrentPath() + " > copy test.txt ./");
        execute("copy test.txt ./", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        System.out.println();

        // remove file
        out.println(playerOS.getCurrentPath() + " > del copy-test.txt");
        execute("del copy-test.txt", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        System.out.println();

        // ping test
        try {
            execute("ping " + network.getRandomIP(), playerOS, SUDO);
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

    /**
     * Execute input commandline from user
     *
     * @param commandLine input
     * @param activeOS    from current computer
     * @param accessLevel to check for
     */
    private void execute(String commandLine, OS activeOS, int accessLevel) {
        List<String> cmdTokens = commandLineSplitter(commandLine);
        String cmdName = cmdTokens.get(0);
        Command command;
        if (activeOS.hasCommand(cmdName)) {
            command = activeOS.getFromCommandSet(cmdName);
            if (cmdTokens.size() == 2
                    && (cmdTokens.get(1).equals("-h") || cmdTokens.get(1).equals("help"))) {
                System.out.println(command.getDescription());
            } else {
                if (cmdTokens.size() - 1 != command.getParamNbr())
                    System.err.println("Missing params");
                else {
                    switch (cmdName) {
                        case "ls":
                        case "dir":
                        case "ld":
                            activeOS.list(accessLevel);
                            break;
                        case "cd":
                        case "chdir":
                            activeOS.changeDirectory(cmdTokens.get(1), activeOS.getCurrentFSPosition(), accessLevel);
                            break;
                        case "cp":
                        case "copy":
                        case "cpy":
                            activeOS.copy(cmdTokens.get(1), cmdTokens.get(2), accessLevel);
                            break;
                        case "rm":
                        case "del":
                        case "rem":
                            activeOS.remove(cmdTokens.get(1), accessLevel);
                            break;
                        case "ping":
                            activeOS.ping(network, cmdTokens.get(1));
                            break;
                        case "vim":
                        case "note":
                        case "nano":
                            System.out.println(activeOS.readFromFile(cmdTokens.get(1), accessLevel));
                            break;
                        case "echo":
                            activeOS.writeToFile(cmdTokens.get(1), cmdTokens.get(2), accessLevel);
                            break;
                        case "chmod":
                        case "mode":
                        case "perm":
                            activeOS.changeMode(cmdTokens.get(1), cmdTokens.get(2), activeOS.getFsRoot(), accessLevel);
                            break;
                    }
                }
            }
        } else System.err.println(cmdName + " is not recognized as an internal or external command");
    }

    /**
     * Splits the input commandline at whitespace except if surrounded by quotes
     *
     * @param commandLine input
     * @return List
     */
    private List<String> commandLineSplitter(String commandLine) {
        List<String> cmdTokens = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(commandLine);
        while (m.find())
            cmdTokens.add(m.group(1).replace("\"", ""));
        return cmdTokens;
    }
}
