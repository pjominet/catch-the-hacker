package tech.clusterfunk;

import tech.clusterfunk.game.Game;

public class Main {

    public static final String CONFIG_ROOT = "/configs/";
    public static final int MAX_ALLOWED_ACCESSLEVEL = 5;
    public static final int SUDO = MAX_ALLOWED_ACCESSLEVEL + 1;

    public static void main(String[] args) {
        Game game = new Game();
        game.debug();
        //game.run();
    }
}
