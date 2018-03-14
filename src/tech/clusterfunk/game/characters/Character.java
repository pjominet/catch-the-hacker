package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

interface Character {
    String getName();
    Computer getComputer();
    @Override
    String toString();
}
