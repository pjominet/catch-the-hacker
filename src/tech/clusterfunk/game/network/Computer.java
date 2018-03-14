package tech.clusterfunk.game.network;

import tech.clusterfunk.game.systems.OS;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.StringJoiner;


public class Computer {
    private String ip;
    private OS os;
    private int accessLevel;
    private int crytoCoins;
    private String user;

    private String randomIPAddress() {
        int min = 1;
        int max = 250;
        Random rnd = new Random(System.currentTimeMillis());
        StringJoiner joiner = new StringJoiner(".");
        for (int i = 0; i < 5; i++) {
            String byteblock = String.valueOf(rnd.nextInt(max - min) + min);
            joiner.add(byteblock);
        }
        return joiner.toString();
    }

    private OS randomOS() {
        OS os;
        Random rnd = new Random(System.currentTimeMillis());
        int result = rnd.nextInt(2);
        if (result == 0) os = new OS("DOORS");
        else if (result == 1) os = new OS("OSY");
        else os = new OS("LOONIX");
        return os;
    }

    private String loadGenericUser(int userId) {
        String config = "src/tech/clusterfunk/configs/USERS.txt";
        String user = null;
        int lineNbr = 0;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config),
                StandardCharsets.UTF_8)) {
            for (String line; (line = reader.readLine()) != null; ) {
                user = line;
                if (++lineNbr == userId) break;
            }
            reader.close();
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("User list not found: " + config);
        }
        return user;
    }

    Computer(int userId) {
        this.ip = randomIPAddress();
        this.os = randomOS();
        this.user = loadGenericUser(userId);

        Random rnd = new Random(System.currentTimeMillis());
        this.accessLevel = rnd.nextInt(4) + 1;
        this.crytoCoins = rnd.nextInt(90) + 10;
    }

    public Computer(String osName, int accessLevel, String user) {
        this.ip = randomIPAddress();
        this.os = new OS(osName);
        this.accessLevel = accessLevel;
        this.crytoCoins = 0;
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public int getCrytoCoins() {
        return crytoCoins;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public OS getOs() {
        return os;
    }

    public String getUser() {
        return user;
    }

    public void installNewOS(String osName) {
        this.os = new OS(osName);
    }

    @Override
    public String toString() {
        return "OS: " + os.getName() + ", Address:" + ip + ", User:" + user + ", Access Level:" + accessLevel + ", " +
                "cryptoCoins:" + crytoCoins;
    }
}
