package tech.clusterfunk.game.systems.network;

import tech.clusterfunk.game.characters.NPC;
import tech.clusterfunk.util.exceptions.FatalException;
import tech.clusterfunk.util.exceptions.InvalidIPException;
import tech.clusterfunk.util.exceptions.UnknownIPException;

import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void addComputer(Computer computer) {
        network.put(computer.getIP(), computer);
    }

    public Computer getComputerByIP(String ip) throws InvalidIPException, UnknownIPException, FatalException {
        if (isComputerAt(ip)) return network.get(ip);
        else throw new FatalException();
    }

    public String getRandomIP() throws FatalException {
        Iterator<Map.Entry<String, Computer>> i = network.entrySet().iterator();
        if (i.hasNext()) {
            Map.Entry<String, Computer> entry = i.next();
            return entry.getKey();
        } else throw new FatalException();
    }

    public boolean isComputerAt(String ip) throws InvalidIPException, UnknownIPException {
        if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            for (Map.Entry<String, Computer> entry : network.entrySet()) {
                if (ip.equals(entry.getKey()))
                    return true;
            }
        } else throw new InvalidIPException("Invalid IP address");
        throw new UnknownIPException("Request timed out\n\tNo reachable machine at " + ip);
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
