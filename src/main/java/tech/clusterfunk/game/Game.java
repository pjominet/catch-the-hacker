package tech.clusterfunk.game;

import tech.clusterfunk.game.characters.Hacker;
import tech.clusterfunk.game.characters.Player;
import tech.clusterfunk.game.systems.network.Computer;
import tech.clusterfunk.game.systems.network.Network;
import tech.clusterfunk.game.systems.operatingsystem.Command;
import tech.clusterfunk.game.systems.operatingsystem.OS;
import tech.clusterfunk.util.ConfigLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tech.clusterfunk.Main.*;

/**
 * Manage main game flow and all user interaction
 */
public class Game {
    public static int DIFFICULTY;
    public static int TURNS;

    private Player player;
    private Hacker hacker;
    private Network network;

    private Map<String, String> initConfig;

    public Game() {
        initConfig = ConfigLoader.loadInitConfig();
    }

    /**
     * Initializes all default game values
     *
     * @param playerName     to be chosen by the user
     * @param playerOS       to be chosen by the user
     * @param playerUsername to be chosen by the user
     */
    private void init(String playerName, String playerOS, String playerUsername) {
        out.println(">> Setting up game...");

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
        out.print(">> Please select game difficulty (0=easy, 1=medium, 2=hard): ");
        DIFFICULTY = in.nextInt();
        */
        // for debugging automation
        DIFFICULTY = 0;
        TURNS = Integer.valueOf(initConfig.get("turns")) - DIFFICULTY;

        out.println(">> Done!");
    }

    /**
     * Run a debug mode for testing purposes
     */
    public void debug() {

        out.println("=== Running Debug Mode ===\n");

        init("Patrick", "DOORS", "blyatrick");
        OS playerOS = player.getComputer().getOS();
        out.println();

        // debug stats
        out.println("--- Player stats ---");
        out.println(player.toString());

        out.println();
        out.println("--- Hacker stats ---");
        out.println(hacker.toString());

        out.println();
        out.println("--- Network map ---");
        out.println(network.toString());

        /* command simulation for debugging */
        out.println("--- Command simulation on Player OS ---");
        // list
        out.println(playerOS.getCurrentPrompt() + "dir");
        execute("dir", playerOS, SUDO);
        out.println();

        // help
        out.println(playerOS.getCurrentPrompt() + "mode -h");
        execute("mode -h", playerOS, SUDO);
        out.println();
        // change permission
        out.println(playerOS.getCurrentPrompt() + "mode +w \"Program Data\"");
        execute("mode +w \"Program Data\"", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        out.println();

        //change directory
        out.println(playerOS.getCurrentPrompt() + "chdir \"Program Data\"");
        execute("chdir \"Program Data\"", playerOS, SUDO);

        // write to file
        out.println(playerOS.getCurrentPrompt() + "echo \"This is some content\" test.txt");
        execute("echo \"This is some content\" test.txt", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        out.println();

        //read from file
        out.println(playerOS.getCurrentPrompt() + "note test.txt");
        execute("note test.txt", playerOS, SUDO);
        out.println();

        // copy test
        out.println(playerOS.getCurrentPrompt() + "copy test.txt ./");
        execute("copy test.txt ./", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        out.println();

        // remove file
        out.println(playerOS.getCurrentPrompt() + "del copy-test.txt");
        execute("del copy-test.txt", playerOS, SUDO);
        execute("dir", playerOS, SUDO);
        out.println();

        // ping test
        execute("ping " + network.getRandomIP(), playerOS, SUDO);
    }

    public void run() {
        out.println("=== Catch The Hacker ===\n");

        String lastInput = newGame();
        execute(lastInput, player.getComputer().getOS(), SUDO);

        in.close();
    }

    /**
     * Start and setup a new game
     *
     * @return String
     */
    private String newGame() {
        out.print("It is your first day as cybersecurity employee at CySec.\n" +
                "You walk to your office, take a seat at your new desk and boot your company computer.\n" +
                "The screen flashes briefly and a prompt appears...\n");
        out.print("\n>> Please enter your name: ");
        String name = in.nextLine();

        out.println("\nAs the system processes your name a new prompt appears...");
        out.println("\n>> Welcome to CySec " + name + "!");
        String os = "";
        ask:
        while (true) {
            out.print(">> Please choose an Operation System\n\t(1)DOORS (2)LOONIX (3)OSY: ");
            switch (in.nextInt()) {
                case 1:
                    os = "DOORS";
                    break;
                case 2:
                    os = "LOONIX";
                    break;
                case 3:
                    os = "OYS";
                    break;
                default:
                    err.println(">> Illegal Argument");
                    break ask;
            }
        }

        out.println("\nThe system installer begins to run and it prompts you again...");
        out.print("\n>> Please provide a username: ");
        String nick = in.next();

        init(name, os, nick);
        OS playerOS = player.getComputer().getOS();

        out.print("\nAs installer finishes, you are being greeted by the welcome screen of " +
                playerOS.getName() + ".\n"
                + "You launch the terminal and start working...\n");
        // change to home directory
        playerOS.changeDirectory("~", playerOS.getFsRoot(), player.getSkill());
        out.print("\n" + playerOS.getCurrentPrompt());

        return in.next();
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
                out.println(command.toString());
            } else {
                if (cmdTokens.size() - 1 != command.getParamNbr())
                    err.println("Missing params");
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
                            activeOS.readFromFile(cmdTokens.get(1), accessLevel);
                            break;
                        case "echo":
                            activeOS.writeToFile(cmdTokens.get(1), cmdTokens.get(2), accessLevel);
                            break;
                        case "chmod":
                        case "mode":
                        case "perm":
                            activeOS.changeMode(cmdTokens.get(1), cmdTokens.get(2), activeOS.getFsRoot(), accessLevel);
                            break;
                        case "nmap":
                        case "netmap":
                            activeOS.networkMap(network);
                            break;
                    }
                }
            }
        } else err.println(cmdName + " is not recognized as an internal or external command");
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
