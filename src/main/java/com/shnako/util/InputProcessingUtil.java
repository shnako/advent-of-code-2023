package com.shnako.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class InputProcessingUtil {
    public static List<String> readInputLines(String day) throws IOException {
        String inputFile = String.format("src/main/java/com/shnako/solutions/day%s/input.txt", day);
        try (Stream<String> stream = Files.lines(Paths.get(inputFile))) {
            return stream.collect(Collectors.toList());
        }
    }

    public static char[][] readCharGrid(String day) throws IOException {
        return readInputLines(day)
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }
}
