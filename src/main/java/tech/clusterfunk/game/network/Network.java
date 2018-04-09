package tech.clusterfunk.game.network;

import tech.clusterfunk.game.characters.NPC;
import tech.clusterfunk.util.exceptions.NoComputerAtThisAddressException;

import java.util.Formatter;
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
        }
    }

    public void addComputer(Computer computer) {
        network.put(computer.getIP(), computer);
    }

    public Computer findComputerAt(String ip) throws NoComputerAtThisAddressException {
        if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            for (Map.Entry<String, Computer> entry : network.entrySet()) {
                if (entry.getValue().getIP().equals(ip))
                    return entry.getValue();
            }
        }
        throw new NoComputerAtThisAddressException("No Computer found at " + ip);
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
