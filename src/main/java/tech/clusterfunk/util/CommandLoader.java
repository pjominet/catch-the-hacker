package tech.clusterfunk.util;

import tech.clusterfunk.game.systems.operatingsystem.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static tech.clusterfunk.Main.CONFIG_ROOT;
import static tech.clusterfunk.Main.err;

public class CommandLoader {

    public static List<Command> loadCommandSet(String os) {
        String config = CONFIG_ROOT + os.toLowerCase() + "_cs.config";
        String pattern = "([a-z]+):(.*):(.+)";
        List<Command> commandSet = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        CommandLoader.class.getResourceAsStream(config),
                        StandardCharsets.UTF_8))
        ) {
            Pattern regex = Pattern.compile(pattern);
            Matcher m;
            for (String line; (line = reader.readLine()) != null; ) {
                m = regex.matcher(line);
                if (m.find()) {
                    List<String> params = new ArrayList<>();
                    if (!m.group(2).equals(""))
                         params = new ArrayList<>(Arrays.asList(m.group(2).split(",")));
                    commandSet.add(new Command(m.group(1), params, m.group(3), os));
                }
            }
        } catch (IOException e) {
            err.println("File not found: " + config);
            System.exit(1);
        }
        return commandSet;
    }

    public static List<Command> loadDefaultCommands() {
        String config = CONFIG_ROOT + "player_defaults.config";
        String pattern = "([A-Z]+):(.*)";
        List<Command> defaultCommands = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        CommandLoader.class.getResourceAsStream(config),
                        StandardCharsets.UTF_8))
        ) {
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
                            if (defaultCmd.equals(cmd.getName())) {
                                defaultCommands.add(cmd);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            err.println("File not found: " + config);
            System.exit(1);
        }
        return defaultCommands;
    }
}
