package tech.clusterfunk.game.network;

import tech.clusterfunk.game.characters.NPC;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class Network {
    private List<Computer> network;
    private List<String> addressList;

    public Network(int size) {
        network = new ArrayList<>();
        try {
            for (int i = 0; i < size; i++) {
                network.add(new NPC(i).getComputer());
                // wait to be sure next rnd seed is different
                Thread.sleep(7);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        generateAddressList();
    }

    private void generateAddressList() {
        addressList = new ArrayList<>();
        for (Computer computer : network) {
            addressList.add(computer.getIp());
        }
    }

    public void addComputer(Computer computer) {
        network.add(computer);
        addressList.add(computer.getIp());
    }

    // TODO implement custom invalid address exception
    public Computer findComputerAt(String ip) {
        Computer found = null;
        if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            for (Computer computer : network) {
                if (computer.getIp().equals(ip))
                    found = computer;
            }
        }
        return found;
    }

    public List<Computer> getNetwork() {
        return network;
    }

    public List<String> getAddressList() {
        return addressList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Formatter formatter = new Formatter(builder);
        int index = 0;
        for (String address : addressList) {
            formatter.format("%-15s", address);
            if (++index % 4 == 0) {
                builder.append("\n");
            } else builder.append("  ");
        }
        return builder.toString();
    }
}
