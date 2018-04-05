package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.util.IOHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static tech.clusterfunk.Main.CONFIG_ROOT;

public class NPC extends Character {
    private int awareness;

    // TODO: add custom exception exception when no username could be loaded
    private String loadGenericUserName(int userId) {
        String config = CONFIG_ROOT + "USERS.txt";
        String userName = "";
        int lineNbr = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        IOHandler.class.getResourceAsStream(config),
                        StandardCharsets.UTF_8
                )
        )) {
            for (String name; (name = reader.readLine()) != null; ) {
                userName = name;
                if (lineNbr++ == userId) break;
            }
            reader.close();
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("User list not found: " + config);
        }
        return userName;
    }

    public NPC(int userID) {
        super(null, null);
        this.name = loadGenericUserName(userID);
        this.computer = new Computer(this.name);
        Random rnd = new Random(System.currentTimeMillis());
        this.awareness = rnd.nextInt(5 - 1) + 1;
    }

    NPC(String name, Computer computer, int awareness) {
        super(name, computer);
        this.awareness = awareness;
    }

    public int getAwareness() {
        return awareness;
    }

    public void setAwareness(int awareness) {
        this.awareness = awareness;
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + "), AWARENESS=" + awareness;
    }
}
