package tech.clusterfunk.game.network;

import tech.clusterfunk.game.systems.OS;

import java.util.Random;
import java.util.StringJoiner;


public class Computer {
    private String ip;
    private OS os;
    private int accessLevel;
    private int crytoCoins;

    private String randomIPAddress() {
        int min = 1;
        int max = 250;
        Random rnd = new Random(System.currentTimeMillis());
        StringJoiner joiner = new StringJoiner(".");
        for (int i = 0; i < 4; i++) {
            String byteblock = String.valueOf(rnd.nextInt(max - min) + min);
            joiner.add(byteblock);
        }
        return joiner.toString();
    }

    private OS randomOS(String user) {
        OS os;
        Random rnd = new Random(System.currentTimeMillis());
        int result = rnd.nextInt(3);
        if (result == 0) os = new OS("DOORS", user);
        else if (result == 1) os = new OS("OSY", user);
        else os = new OS("LOONIX", user);
        return os;
    }

    public Computer(String user) {
        this.ip = randomIPAddress();
        this.os = randomOS(user);

        Random rnd = new Random(System.currentTimeMillis());
        this.accessLevel = rnd.nextInt(4) + 1;
        this.crytoCoins = rnd.nextInt(90) + 10;
    }

    public Computer(String osName, int accessLevel, String user) {
        this.ip = randomIPAddress();
        this.os = new OS(osName, user);
        this.accessLevel = accessLevel;
        this.crytoCoins = 0;
    }

    public String getIP() {
        return ip;
    }

    public int getCrytoCoins() {
        return crytoCoins;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public OS getOS() {
        return os;
    }

    public void installNewOS(String osName, String user) {
        this.os = new OS(osName, user);
    }

    @Override
    public String toString() {
        return "OS: " + os.getName() + ", IP: " + ip + ", access level: " + accessLevel + ", crypto coins: " + crytoCoins;
    }
}
