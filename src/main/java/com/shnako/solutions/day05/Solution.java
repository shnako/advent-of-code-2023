package com.shnako.solutions.day05;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Solution extends SolutionBase {
    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String runPart1() throws IOException {
        Almanac almanac = parseInput();
        long result = almanac.seeds
                .stream()
                .mapToLong(almanac::findLocation)
                .min()
                .getAsLong();
        return String.valueOf(result);
    }

    @Override
    public String runPart2() {
        return "456";
    }

    private Almanac parseInput() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());

        List<Long> seeds = Arrays.stream(input.get(0).substring(7).split(" "))
                .map(Long::parseLong)
                .toList();

        List<AlmanacMap> almanacMaps = new ArrayList<>(7);
        AlmanacMap almanacMap = null;
        for (String line : input.subList(1, input.size())) {
            if (StringUtils.isBlank(line)) {
                almanacMap = new AlmanacMap();
                almanacMaps.add(almanacMap);
                continue;
            }
            if (Character.isAlphabetic(line.charAt(0))) {
                continue;
            }
            List<Long> rangeNumbers = Arrays.stream(line.split(" "))
                    .map(Long::parseLong)
                    .toList();

            Range range = new Range(rangeNumbers.get(0), rangeNumbers.get(1), rangeNumbers.get(2));
            Objects.requireNonNull(almanacMap).addRange(range);
        }
        return new Almanac(seeds, almanacMaps);
    }

    private record Almanac(List<Long> seeds, List<AlmanacMap> almanacMaps) {
        private long findLocation(long seed) {
            long currentNumber = seed;
            for (AlmanacMap almanacMap : almanacMaps) {
                currentNumber = almanacMap.getDestination(currentNumber);
            }
            return currentNumber;
        }
    }

    private class AlmanacMap {
        private final List<Range> ranges = new ArrayList<>();

        private void addRange(Range range) {
            ranges.add(range);
        }

        private long getDestination(long source) {
            for (Range range : ranges) {
                if (source >= range.sourceStart && source <= range.sourceStart + range.length) {
                    long offset = source - range.sourceStart;
                    return range.destinationStart + offset;
                }
            }
            return source;
        }

    }

    private record Range(long destinationStart, long sourceStart, long length) {
    }
}