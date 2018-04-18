package tech.clusterfunk;

import tech.clusterfunk.game.Game;

import java.io.PrintStream;
import java.util.Scanner;

public class Main {

    public static final String CONFIG_ROOT = "/configs/";
    public static final int MAX_ALLOWED_ACCESSLEVEL = 5;
    public static final int SUDO = MAX_ALLOWED_ACCESSLEVEL + 1;

    public final static PrintStream out = new PrintStream(System.out);
    public final static PrintStream err = new PrintStream(System.err);
    public final static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        Game game = new Game();
        game.debug();
        //game.run();
    }
}
