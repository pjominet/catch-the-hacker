package tech.clusterfunk.game.systems.network;

import tech.clusterfunk.game.characters.NPC;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static tech.clusterfunk.Main.err;
import static tech.clusterfunk.game.Game.DIFFICULTY;

public class Network {
    private Map<String, Computer> network;

    public Network(int size) {
        network = new ConcurrentHashMap<>();
        try {
            for (int i = 0; i < size; i++) {
                Computer computer = new NPC(i).getComputer();
                network.put(computer.getIP(), computer);
                // wait to be sure next rnd seed is different
                Thread.sleep(7);
            }
            createMeshTopology();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void addComputer(Computer computer) {
        network.put(computer.getIP(), computer);
    }

    public Computer getComputerByIP(String ip) {
        return network.get(ip);
    }

    public String getRandomIP() {
        Random generator = new Random();
        Object[] values = network.values().toArray();
        Computer computer = (Computer) values[generator.nextInt(values.length)];
        return computer.getIP();
    }

    private void createMeshTopology() {
        Iterator<Map.Entry<String, Computer>> i = network.entrySet().iterator();
        if (i.hasNext()) {
            Map.Entry<String, Computer> entry = i.next();
            List<String> knownHosts = new ArrayList<>();
            for (int j = 0; j < 4 - DIFFICULTY; j++) {
                knownHosts.add(getRandomIP());
                entry.getValue().setKnownHosts(knownHosts);
            }
        }
    }

    private boolean isValidIP(String ip) {
        if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"))
            return true;
        else {
            err.println("Invalid IP address");
            return false;
        }
    }

    public boolean isComputerAt(String ip) {
        if (isValidIP(ip)) {
            for (Map.Entry<String, Computer> entry : network.entrySet()) {
                if (ip.equals(entry.getKey()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder);
        int index = 0;
        for (Map.Entry<String, Computer> entry : network.entrySet()) {
            formatter.format("%-15s", entry.getValue().getIP());
            if (++index % 4 == 0) {
                builder.append("\n");
            } else builder.append("  ");
        }
        return builder.toString();
    }
}
