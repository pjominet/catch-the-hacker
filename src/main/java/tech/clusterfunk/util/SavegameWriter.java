package tech.clusterfunk.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SavegameWriter {
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
                    System.err.println("File already exists, no writing occurred");
                    System.exit(1);
                } else {
                    try {
                        Files.delete(Paths.get(path));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }

    }
}
