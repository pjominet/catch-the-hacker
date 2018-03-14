package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

public interface Hacker extends Character {
    void hack(Computer computer);
}
