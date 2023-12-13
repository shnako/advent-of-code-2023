package com.shnako.solutions.day13;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/*
We start by parsing each desert into Pattern objects,
containing the desert and a list of unique hashes generated for each row and column.

Part 1:
We look through the list of row and column hashes for adjacent ones that are identical.
We then go away in both directions from the reflection point and verify that they're all identical.
The result is calculated based on the reflection points found.

Part 2:
We do the same as part 1, but we compare the hashes looking for reflection points that are
mismatching by exactly one hash when checking in both directions.
Once we found one, we check the desert where the hashes mismatch to confirm that there's only 1 point mismatching.
We know that there will be exactly 1 of these in each desert, so there will be only 1 matching the above criteria.
The result is calculated based on the reflection points found.
 */
public class Solution extends SolutionBase {
    //region Part 1

    @Override
    public String runPart1() throws IOException {
        List<Pattern> patterns = parseInput();

        int result = 0;
        for (Pattern pattern : patterns) {
            int reflectionIndexRow = findReflectionIndexRow(pattern);
            if (reflectionIndexRow >= 0) {
                result += (reflectionIndexRow + 1) * 100;
            }
            int reflectionIndexCol = findReflectionIndexCol(pattern);
            if (reflectionIndexCol >= 0) {
                result += reflectionIndexCol + 1;
            }
        }

        return String.valueOf(result);
    }

    // region Part 1 row checks

    private int findReflectionIndexRow(Pattern pattern) {
        for (int i = 0; i < pattern.desert.length - 1; i++) {
            if (isValidRowReflectionAtIndex(pattern, i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isValidRowReflectionAtIndex(Pattern pattern, int reflectionRowIndex) {
        for (int i = 0; reflectionRowIndex - i >= 0 && reflectionRowIndex + i < pattern.rowHashes.size() - 1; i++) {
            if (!Objects.equals(pattern.rowHashes.get(reflectionRowIndex - i), pattern.rowHashes.get(reflectionRowIndex + i + 1))) {
                return false;
            }
        }
        return true;
    }

    // endregion

    // region Part 1 column checks

    private int findReflectionIndexCol(Pattern pattern) {
        for (int i = 0; i < pattern.desert[0].length - 1; i++) {
            if (isValidColReflectionAtIndex(pattern, i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isValidColReflectionAtIndex(Pattern pattern, int reflectionColIndex) {
        for (int i = 0; reflectionColIndex - i >= 0 && reflectionColIndex + i < pattern.columnHashes.size() - 1; i++) {
            if (!Objects.equals(pattern.columnHashes.get(reflectionColIndex - i), pattern.columnHashes.get(reflectionColIndex + i + 1))) {
                return false;
            }
        }
        return true;
    }

    // endregion

    // endregion

    // region Part 2
    @Override
    public String runPart2() throws IOException {
        List<Pattern> patterns = parseInput();

        int result = 0;
        for (Pattern pattern : patterns) {
            int offByOneIndex = findReflectionIndexRowToleratingOneFailure(pattern);
            if (offByOneIndex >= 0) {
                result += 100 * (offByOneIndex + 1);
            } else {
                offByOneIndex = findReflectionIndexColumnToleratingOneFailure(pattern);
                result += offByOneIndex + 1;
            }
        }

        return String.valueOf(result);
    }

    // region Part 2 row checks

    private int findReflectionIndexRowToleratingOneFailure(Pattern pattern) {
        for (int i = 0; i < pattern.desert.length - 1; i++) {
            if (isValidRowReflectionAtIndexToleratingOneFailure(pattern, i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isValidRowReflectionAtIndexToleratingOneFailure(Pattern pattern, int reflectionRowIndex) {
        int[] failureIndexes = null;
        for (int i = 0; reflectionRowIndex - i >= 0 && reflectionRowIndex + i < pattern.rowHashes.size() - 1; i++) {
            int indexA = reflectionRowIndex - i;
            int indexB = reflectionRowIndex + i + 1;
            if (!Objects.equals(pattern.rowHashes.get(indexA), pattern.rowHashes.get(indexB))) {
                if (failureIndexes != null) {
                    return false;
                } else {
                    failureIndexes = new int[]{indexA, indexB};
                }
            }
        }
        return failureIndexes != null && areRowsOffByExactlyOne(pattern, failureIndexes);
    }

    private boolean areRowsOffByExactlyOne(Pattern pattern, int[] indexes) {
        boolean failureFound = false;
        for (int i = 0; i < pattern.desert[0].length; i++) {
            if (pattern.desert[indexes[0]][i] != pattern.desert[indexes[1]][i]) {
                if (failureFound) {
                    return false;
                } else {
                    failureFound = true;
                }
            }
        }
        return failureFound;
    }

    // endregion

    // region Part 2 column checks

    private int findReflectionIndexColumnToleratingOneFailure(Pattern pattern) {
        for (int i = 0; i < pattern.desert[0].length - 1; i++) {
            if (isValidColumnReflectionAtIndexToleratingOneFailure(pattern, i)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isValidColumnReflectionAtIndexToleratingOneFailure(Pattern pattern, int reflectionColIndex) {
        int[] failureIndexes = null;
        for (int i = 0; reflectionColIndex - i >= 0 && reflectionColIndex + i < pattern.columnHashes.size() - 1; i++) {
            int indexA = reflectionColIndex - i;
            int indexB = reflectionColIndex + i + 1;
            if (!Objects.equals(pattern.columnHashes.get(indexA), pattern.columnHashes.get(indexB))) {
                if (failureIndexes != null) {
                    return false;
                } else {
                    failureIndexes = new int[]{indexA, indexB};
                }
            }
        }
        return failureIndexes != null && areColumnsOffByExactlyOne(pattern, failureIndexes);
    }

    private boolean areColumnsOffByExactlyOne(Pattern pattern, int[] indexes) {
        boolean failureFound = false;
        for (int i = 0; i < pattern.desert.length; i++) {
            if (pattern.desert[i][indexes[0]] != pattern.desert[i][indexes[1]]) {
                if (failureFound) {
                    return false;
                } else {
                    failureFound = true;
                }
            }
        }
        return failureFound;
    }

    // endregion

    // endregion

    // region Input processing

    private List<Pattern> parseInput() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        int startIndex = 0;
        List<Pattern> result = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            if (StringUtils.isBlank(input.get(i))) {
                result.add(parsePattern(input.subList(startIndex, i)));
                startIndex = i + 1;
            }
        }
        result.add(parsePattern(input.subList(startIndex, input.size())));
        return result;
    }

    private Pattern parsePattern(List<String> patternInput) {
        char[][] patternChars = patternInput
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);

        List<Integer> rowHashes = Arrays.stream(patternChars)
                .map(Arrays::hashCode)
                .toList();

        List<Integer> columnHashes = new ArrayList<>();
        for (int col = 0; col < patternChars[0].length; col++) {
            char[] columnElements = new char[patternChars.length];
            for (int row = 0; row < patternChars.length; row++) {
                columnElements[row] = patternChars[row][col];
            }
            columnHashes.add(Arrays.hashCode(columnElements));
        }

        return new Pattern(patternChars, rowHashes, columnHashes);
    }

    // endregion

    private record Pattern(char[][] desert, List<Integer> rowHashes, List<Integer> columnHashes) {
    }
}