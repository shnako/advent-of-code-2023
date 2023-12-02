package com.shnako.solutions.day01;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/*
Part 1:
We simply look for the first and last digit and then combine them to return the result.

Part 2:
We determine the index of the first and last occurrence of each digit, whether a digit or a string, in each string.
For the first letter we get the minimum index of the first occurrence and for the second we get the maximum index of the last occurrence.
Then we map back to actual digits and return the result.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws Exception {
        List<String> inputLines = InputProcessingUtil.readInputLines(getDay());
        return ((Integer) inputLines.stream()
                .map(this::extractCalibrationValue)
                .mapToInt(Integer::intValue)
                .sum())
                .toString();
    }

    private int extractCalibrationValue(String line) {
        int firstDigit = 0, secondDigit = 0;
        for (int i = 0; i < line.length(); i++) {
            if (Character.isDigit(line.charAt(i))) {
                firstDigit = Character.getNumericValue(line.charAt(i));
                break;
            }
        }
        for (int i = line.length() - 1; i >= 0; i--) {
            if (Character.isDigit(line.charAt(i))) {
                secondDigit = Character.getNumericValue(line.charAt(i));
                break;
            }
        }
        return firstDigit * 10 + secondDigit;
    }

    @Override
    public String runPart2() throws Exception {
        List<String> inputLines = InputProcessingUtil.readInputLines(getDay());
        return ((Integer) inputLines.stream()
                .map(this::extractComplexCalibrationValue)
                .mapToInt(Integer::intValue)
                .sum())
                .toString();
    }

    private static final List<String> DIGITS = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9",
            "one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
    private static final Map<String, Integer> DIGIT_MAP = Map.ofEntries(
            entry("1", 1),
            entry("2", 2),
            entry("3", 3),
            entry("4", 4),
            entry("5", 5),
            entry("6", 6),
            entry("7", 7),
            entry("8", 8),
            entry("9", 9),
            entry("one", 1),
            entry("two", 2),
            entry("three", 3),
            entry("four", 4),
            entry("five", 5),
            entry("six", 6),
            entry("seven", 7),
            entry("eight", 8),
            entry("nine", 9)
    );

    @SuppressWarnings({"ComparatorMethodParameterNotUsed", "OptionalGetWithoutIsPresent"})
    private int extractComplexCalibrationValue(String line) {
        String firstDigitStr = DIGITS.stream()
                .map(x -> Pair.of(x, line.indexOf(x)))
                .filter(x -> x.getRight() != -1)
                .min((a, b) -> a.getRight() < b.getRight() ? -1 : 1)
                .get()
                .getLeft();
        int firstDigit = DIGIT_MAP.get(firstDigitStr);
        String secondDigitStr = DIGITS.stream()
                .map(x -> Pair.of(x, line.lastIndexOf(x)))
                .filter(x -> x.getRight() != -1)
                .max((a, b) -> a.getRight() < b.getRight() ? -1 : 1)
                .get()
                .getLeft();
        int secondDigit = DIGIT_MAP.get(secondDigitStr);

        return firstDigit * 10 + secondDigit;
    }
}