package tech.clusterfunk.util;

import tech.clusterfunk.game.systems.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOHandler {
    public static List<Command> loadCommandSet(String os) {
        String config = "src/tech/clusterfunk/configs/" + os + "_CMD.cnf";
        String pattern = "([a-z]+):(.*):(.+)";
        List<Command>commandSet = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config),
                StandardCharsets.UTF_8)) {
            Pattern regex = Pattern.compile(pattern);
            Matcher m;
            for (String line; (line = reader.readLine()) != null; ) {
                m = regex.matcher(line);
                if(m.find()) {
                    List<String> params = new ArrayList<>(Arrays.asList(m.group(2).split(",")));
                    commandSet.add(new Command(m.group(1), params, m.group(2), os));
                }
            }
            reader.close();
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("FileSystem not found: " + config);
        }
        return commandSet;
    }

    public static List<Command> loadDefaultCommands() {
        String config = "src/tech/clusterfunk/configs/PLAYER_CMD.cnf";
        String pattern = "([A-Z]+):(.*)";
        List<Command> defaultCommands = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(config),
                StandardCharsets.UTF_8)) {
            Pattern regex = Pattern.compile(pattern);
            Matcher m;
            for (String line; (line = reader.readLine()) != null; ) {
                m = regex.matcher(line);
                if (m.find()) {
                    String os = m.group(1);
                    List<Command> commandList = loadCommandSet(os);
                    List<String> defaults = new ArrayList<>(Arrays.asList(m.group(2).split(",")));
                    for (String defaultCmd : defaults) {
                        for (Command cmd : commandList) {
                            if(defaultCmd.equals(cmd.getName())) {
                                defaultCommands.add(cmd);
                            }
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("FileSystem not found: " + config);
        }
        return defaultCommands;
    }

    public static void writeToTextFile(String writable, String path) {
        int tries = 0;
        while (true) {
            try {
                Files.createFile(Paths.get(path));
                try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path),
                        StandardCharsets.UTF_8, StandardOpenOption.WRITE)) {
                    writer.write(writable);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                if (++tries == 3) {
                    System.err.println("FileSystem already exists, no writing occurred");
                    break;
                } else {
                    try {
                        Files.delete(Paths.get(path));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

    }
}
