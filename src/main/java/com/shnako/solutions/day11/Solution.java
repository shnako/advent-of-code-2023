package com.shnako.solutions.day11;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
Part 1:
We store the galaxy locations as a list of coordinates. For each galaxy pair,
we calculate the difference between their coordinates on both axis, adding the empty rows or columns between them,
and sum these 2 results together to find the distance between them.
The result is the sum of these distances.

Part 2:
Same as part 1, but each empty row and column is counted as (1 million - 1) instead of just 1.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        return solve(1);
    }

    @Override
    public String runPart2() throws IOException {
        return solve(1000000 - 1);
    }

    private String solve(long emptinessFactor) throws IOException {
        char[][] input = InputProcessingUtil.readCharGrid(getDay());
        Image image = new Image(input);
        long result = 0;
        for (int i = 0; i < image.galaxies.size(); i++) {
            for (int j = i + 1; j < image.galaxies.size(); j++) {
                result += findDistance(image.galaxies.get(i), image.galaxies.get(j), image, emptinessFactor);
            }
        }
        return String.valueOf(result);
    }

    private long findDistance(int[] a, int[] b, Image image, long emptinessFactor) {
        long rowDistance = (Math.abs(a[0] - b[0]) + emptinessFactor * findEmptySpaces(a[0], b[0], image.emptyRows));
        long colDistance = (Math.abs(a[1] - b[1]) + emptinessFactor * findEmptySpaces(a[1], b[1], image.emptyColumns));
        return rowDistance + colDistance;
    }

    private int findEmptySpaces(int x1, int x2, List<Integer> emptySpaces) {
        if (x1 > x2) {
            x1 = x1 ^ x2;
            x2 = x1 ^ x2;
            x1 = x1 ^ x2;
        }
        int result = 0;
        for (int space : emptySpaces) {
            if (x1 < space && space < x2) {
                result++;
            }
        }
        return result;
    }

    private static class Image {
        private final char[][] image;
        private final List<int[]> galaxies;
        private final List<Integer> emptyRows;
        private final List<Integer> emptyColumns;

        private Image(char[][] image) {
            this.image = image;
            this.galaxies = findGalaxies();
            this.emptyRows = findEmptiness(true);
            this.emptyColumns = findEmptiness(false);
        }

        private List<int[]> findGalaxies() {
            List<int[]> result = new ArrayList<>();
            for (int i = 0; i < image.length; i++) {
                for (int j = 0; j < image[i].length; j++) {
                    if (image[i][j] == '#') {
                        result.add(new int[]{i, j});
                    }
                }
            }
            return result;
        }

        private List<Integer> findEmptiness(boolean row) {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < image.length; i++) {
                boolean isEmpty = true;
                for (int[] galaxy : galaxies) {
                    if ((row && galaxy[0] == i) || (!row && galaxy[1] == i)) {
                        isEmpty = false;
                        break;
                    }
                }
                if (isEmpty) {
                    result.add(i);
                }
            }
            return result;
        }
    }
}