package com.shnako.solutions.day06;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/*
Very straightforward problem today. Part 1 and part 2 solutions are the same,
the only difference is that we strip all the spaces from the input before parsing for part 2,
which results in a single large record rather than multiple smaller ones.

We simply calculate all the possible distances and determine how many of them are better than the record.
There are ways to optimise this, but bruteforce works fine and fast so no point in doing it.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        Map<Long, Long> records = parseInput(input);
        long result = solve(records);
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(x -> x.replace(" ", ""))
                .collect(Collectors.toList());
        Map<Long, Long> records = parseInput(input);
        long result = solve(records);
        return String.valueOf(result);
    }

    private long solve(Map<Long, Long> records) {
        List<Long> winOptions = new ArrayList<>(records.size());
        for (long time : records.keySet()) {
            long winOptionsForTime = LongStream.range(0, time)
                    .map(chargeTime -> chargeTime * (time - chargeTime))
                    .filter(distance -> distance > records.get(time))
                    .count();
            winOptions.add(winOptionsForTime);
        }

        return winOptions.stream().reduce(1L, (a, b) -> a * b);
    }

    private Map<Long, Long> parseInput(List<String> input) {
        List<Long> times = extractNumbersFromLine(input.get(0));
        List<Long> distances = extractNumbersFromLine(input.get(1));

        Map<Long, Long> records = new HashMap<>(times.size());
        for (int i = 0; i < times.size(); i++) {
            records.put(times.get(i), distances.get(i));
        }
        return records;
    }

    private List<Long> extractNumbersFromLine(String line) {
        return Arrays.stream(line.split(":")[1].trim().split(" "))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::parseLong)
                .toList();
    }
}