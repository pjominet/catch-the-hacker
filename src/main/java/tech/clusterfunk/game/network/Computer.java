package tech.clusterfunk.game.network;

import tech.clusterfunk.game.systems.OS;

import java.util.Random;
import java.util.StringJoiner;

import static tech.clusterfunk.Main.MAX_ALLOWED_ACCESSLEVEL;
import static tech.clusterfunk.game.Game.DIFFICULTY;

public class Computer {
    private String ip;
    private OS os;

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
        Random rnd = new Random(System.currentTimeMillis());
        int osChoice = rnd.nextInt(3);
        int minAllowedAccessLevel = 1 + DIFFICULTY;
        int accessLevel = rnd.nextInt(MAX_ALLOWED_ACCESSLEVEL - minAllowedAccessLevel) + minAllowedAccessLevel;

        if (osChoice == 0) return new OS("DOORS", user, accessLevel);
        else if (osChoice == 1) return new OS("OSY", user, accessLevel);
        else return new OS("LOONIX", user, accessLevel);
    }

    public Computer(String user) {
        this.ip = randomIPAddress();
        this.os = randomOS(user);
    }

    public Computer(String osName, String user, int accessLevel) {
        this.ip = randomIPAddress();
        this.os = new OS(osName, user, accessLevel);
    }

    public String getIP() {
        return ip;
    }

    public OS getOS() {
        return os;
    }

    public void installNewOS(String osName, String user, int accessLevel) {
        this.os = new OS(osName, user, accessLevel);
    }

    @Override
    public String toString() {
        return "OS: " + os.getName() + ", IP: " + ip;
    }
}
