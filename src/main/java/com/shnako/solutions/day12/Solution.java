package com.shnako.solutions.day12;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
Part 1:
We use a recursive backtracking algorithm to generate all combinations valid from each point forward.
Compared to an implementation that would just generate all possibilities and check each of them,
this has the advantage of early termination when no solutions are possible, saving a lot of executions.
The result is the sum of valid permutations for each input.

Part 2:
The input here is much larger and executions where there are a lot of ? will run for a very long time.
The solution is to add memoization, which avoids repeating a huge amount of work.
The result is the sum of valid permutations for each input.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Pair<String, List<Integer>>> records = parseInput();
        long result = records
                .stream()
                .mapToLong(record -> findPossibleArrangements(record, 0, -1, -1))
                .sum();
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        List<Pair<String, List<Integer>>> records = parseInput();
        long result = records
                .stream()
                .map(pair -> Pair.of(
                        String.join("?", Collections.nCopies(5, pair.getLeft())),
                        Collections.nCopies(5, pair.getRight()).stream().flatMap(List::stream).collect(Collectors.toList())
                ))
                .mapToLong(record -> findPossibleArrangementsMemoized(record, 0, -1, -1))
                .sum();
        return String.valueOf(result);
    }

    private final Map<MemoizationParameters, Long> cache = new HashMap<>();

    private long findPossibleArrangementsMemoized(Pair<String, List<Integer>> record, int conditionIndex, int orderIndex, int remainingOrderCount) {
        var memoizationParameters = new MemoizationParameters(record, conditionIndex, orderIndex, remainingOrderCount);
        if (cache.containsKey(memoizationParameters)) {
            return cache.get(memoizationParameters);
        } else {
            long result = findPossibleArrangements(record, conditionIndex, orderIndex, remainingOrderCount);
            cache.put(memoizationParameters, result);
            return result;
        }
    }

    private long findPossibleArrangements(Pair<String, List<Integer>> record, int conditionIndex, int orderIndex, int remainingOrderCount) {
        if (conditionIndex == record.getLeft().length()) {
            if (remainingOrderCount <= 0 && orderIndex == record.getRight().size() - 1) {
                return 1;
            } else {
                return 0;
            }
        }

        char currentCondition = record.getLeft().charAt(conditionIndex);
        long hashResults = 0, dotResults = 0;
        if (currentCondition == '#' || currentCondition == '?') {
            hashResults = findPossibleArrangementsForCharacterAtPosition(record, '#', conditionIndex, orderIndex, remainingOrderCount);
        }
        if (currentCondition == '.' || currentCondition == '?') {
            dotResults = findPossibleArrangementsForCharacterAtPosition(record, '.', conditionIndex, orderIndex, remainingOrderCount);
        }

        return hashResults + dotResults;
    }

    private long findPossibleArrangementsForCharacterAtPosition(Pair<String, List<Integer>> record, char condition, int conditionIndex, int orderIndex, int remainingOrderCount) {
        if (condition == '#') {
            if (remainingOrderCount == 0) { // Ran out of damaged springs in this group.
                return 0;
            } else if (remainingOrderCount < 0) {
                if (orderIndex == record.getRight().size() - 1) { // Ran out of orders.
                    return 0;
                } else { // Valid # - first in group
                    return findPossibleArrangementsMemoized(record, conditionIndex + 1, orderIndex + 1, record.getRight().get(orderIndex + 1) - 1);
                }
            } else { // Valid # - not first in group
                return findPossibleArrangementsMemoized(record, conditionIndex + 1, orderIndex, remainingOrderCount - 1);
            }
        } else { // condition == .
            if (remainingOrderCount > 0) { // Found less than expected damaged springs in group.
                return 0;
            } else { // Valid .
                return findPossibleArrangementsMemoized(record, conditionIndex + 1, orderIndex, remainingOrderCount - 1);
            }
        }
    }

    private List<Pair<String, List<Integer>>> parseInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(this::parseInputLine)
                .collect(Collectors.toList());
    }

    private Pair<String, List<Integer>> parseInputLine(String line) {
        String[] components = line.split(" ");
        String conditions = components[0];
        List<Integer> order = Arrays.stream(components[1].split(","))
                .map(Integer::parseInt)
                .toList();
        return Pair.of(conditions, order);
    }

    private record MemoizationParameters(Pair<String, List<Integer>> record, int conditionIndex, int orderIndex,
                                         int remainingOrderCount) {
    }
}