package tech.clusterfunk.game.network;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class Network {
    private List<Computer> network;
    private List<String> addressList;

    public Network(int size) {
        network = new ArrayList<>();
        try {
            for (int i = 0; i < size; i++) {
                network.add(new Computer(i));
                // wait to be sure next rnd seed is different
                Thread.sleep(10);
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

    public void addAddress(String address) {
        addressList.add(address);
    }

    public List<Computer> getNetwork() {
        return network;
    }

    public List<String> getAddressList() {
        return addressList;
    }

    @Override
    public String toString() {
        return addressList.stream().collect(joining(" - "));
    }
}
