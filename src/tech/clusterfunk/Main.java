package tech.clusterfunk;

import tech.clusterfunk.game.Game;

public class Main {

    public static final String CONFIG_ROOT = "resources/configs/";

    public static void main(String[] args) {
        Game game = new Game();
        game.debug();
    }
}
