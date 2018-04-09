package tech.clusterfunk.game.characters;

import tech.clusterfunk.game.network.Computer;
import tech.clusterfunk.util.CommandLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static tech.clusterfunk.Main.CONFIG_ROOT;

public class NPC extends Character {
    int wallet;

    private String loadGenericUserName(int userId) {
        String config = CONFIG_ROOT + "users.list";
        String userName = "";
        int lineNbr = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        CommandLoader.class.getResourceAsStream(config),
                        StandardCharsets.UTF_8
                )
        )) {
            for (String line; (line = reader.readLine()) != null; ) {
                userName = line;
                if (lineNbr++ == userId) break;
            }
        } catch (IOException e) {
            System.err.println("User list not found: " + config);
        }
        return userName;
    }

    public NPC(int userID) {
        super(null, null);
        this.name = loadGenericUserName(userID);
        this.computer = new Computer(this.name);
        Random rnd = new Random(System.currentTimeMillis());
        this.wallet = rnd.nextInt(90) + 10;
    }

    NPC(String name, Computer computer, int wallet) {
        super(name, computer);
        this.wallet = wallet;
    }

    public int getWallet() {
        return wallet;
    }

    public void setWallet(int wallet) {
        this.wallet = wallet;
    }

    @Override
    public String toString() {
        return "NAME=" + name + ", PC=(" + computer + "), WALLET: " + wallet;
    }
}
