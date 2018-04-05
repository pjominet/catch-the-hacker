package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;

abstract class Character {
    String name;
    Computer computer;

    Character(String name, Computer computer) {
        this.name = name;
        this.computer = computer;
    }

    public String getName() {
        return name;
    }

    public Computer getComputer() {
        return computer;
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + ')';
    }
}
