package com.shnako.solutions.day14;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.*;

/*
Part 1:
We move all the movable rocks as far north as possible.
The result is the load calculated as described.

Part 2:
We implement the tilts in the 3 other directions as well and go through all of them for a cycle.
The solution here is based on the observation that the cycles will eventually repeat periodically.
Therefore, we don't need to compute all 1B cycles, we can do it until we detect a repetition and skip lots of cycles.
We keep a hash of the state of movable rocks in a map for each cycle. We know we found a repetition once a hash repeats.
We can then skip as many full repetitions that remain, calculating only the remaining tilts and get to the same result.
The result is the load calculated as described.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        Platform platform = new Platform(input);
        platform.tiltNorth();
        return String.valueOf(platform.calculateLoad());
    }

    @Override
    public String runPart2() throws IOException {
        final int tiltCycles = 1000000000;
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        Platform platform = new Platform(input);
        Map<Integer, Integer> hashToCycleMap = new HashMap<>();
        boolean repetitionFound = false;
        for (int i = 1; i <= tiltCycles; i++) {
            platform.tiltCycle();
            if (!repetitionFound) {
                int hashCode = platform.calculateHashCode();
                if (hashToCycleMap.containsKey(hashCode)) {
                    int repeatingCycleLength = i - hashToCycleMap.get(hashCode);
                    i = i + repeatingCycleLength * ((tiltCycles - i) / repeatingCycleLength);
                    repetitionFound = true;
                } else {
                    hashToCycleMap.put(hashCode, i);
                }
            }
        }
        return String.valueOf(platform.calculateLoad());
    }

    private List<List<Integer>> initializeListOfListOfInt(int size) {
        List<List<Integer>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new ArrayList<>());
        }
        return result;
    }

    @SuppressWarnings("DuplicatedCode")
    private class Platform {
        private final List<List<Integer>> fixedRocks;
        private List<List<Integer>> movingRocks;

        private final int height;
        private final int width;

        private Platform(List<String> input) {
            this.height = input.size();
            this.width = input.get(0).length();
            this.fixedRocks = initializeListOfListOfInt(height);
            this.movingRocks = initializeListOfListOfInt(height);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    switch (input.get(i).charAt(j)) {
                        case '#' -> fixedRocks.get(i).add(j);
                        case 'O' -> movingRocks.get(i).add(j);
                    }
                }
            }
        }

        private int calculateLoad() {
            int result = 0;
            for (int x = 0; x < height; x++) {
                result += (height - x) * movingRocks.get(x).size();
            }
            return result;
        }

        private int calculateHashCode() {
            return movingRocks.stream()
                    .map(List::hashCode)
                    .toList()
                    .hashCode();
        }

        private void tiltCycle() {
            tiltNorth();
            tiltWest();
            tiltSouth();
            tiltEast();
        }

        private void tiltNorth() {
            List<List<Integer>> northGravityColumns = initializeListOfListOfInt(width);

            for (int x = 0; x < height; x++) {
                for (int y : fixedRocks.get(x)) {
                    northGravityColumns.get(y).add(x);
                }
            }

            List<List<Integer>> newMovingRocks = initializeListOfListOfInt(height);
            for (int x = 0; x < height; x++) {
                Collections.sort(movingRocks.get(x));
                for (int y : movingRocks.get(x)) {
                    int finalX = x;
                    int highestFoundRock = northGravityColumns.get(y)
                            .stream()
                            .filter(rockX -> rockX < finalX)
                            .max(Integer::compareTo)
                            .orElse(-1);
                    northGravityColumns.get(y).add(highestFoundRock + 1);
                    newMovingRocks.get(highestFoundRock + 1).add(y);
                }
            }

            movingRocks = newMovingRocks;
        }

        private void tiltSouth() {
            List<List<Integer>> southGravityColumns = initializeListOfListOfInt(width);

            for (int x = 0; x < height; x++) {
                for (int y : fixedRocks.get(x)) {
                    southGravityColumns.get(y).add(x);
                }
            }

            List<List<Integer>> newMovingRocks = initializeListOfListOfInt(height);
            for (int x = height - 1; x >= 0; x--) {
                Collections.sort(movingRocks.get(x));
                Collections.reverse(movingRocks.get(x));
                for (int y : movingRocks.get(x)) {
                    int finalX = x;
                    int lowestFoundRock = southGravityColumns.get(y)
                            .stream()
                            .filter(rockX -> rockX > finalX)
                            .min(Integer::compareTo)
                            .orElse(height);
                    southGravityColumns.get(y).add(lowestFoundRock - 1);
                    newMovingRocks.get(lowestFoundRock - 1).add(y);
                }
            }

            movingRocks = newMovingRocks;
        }

        private void tiltWest() {
            List<List<Integer>> westGravityColumns = initializeListOfListOfInt(height);

            for (int x = 0; x < height; x++) {
                for (int y : fixedRocks.get(x)) {
                    westGravityColumns.get(x).add(y);
                }
            }

            List<List<Integer>> newMovingRocks = initializeListOfListOfInt(height);
            for (int x = 0; x < height; x++) {
                Collections.sort(movingRocks.get(x));
                for (int y : movingRocks.get(x)) {
                    int lowestFoundRock = westGravityColumns.get(x)
                            .stream()
                            .filter(rockY -> rockY < y)
                            .max(Integer::compareTo)
                            .orElse(-1);
                    westGravityColumns.get(x).add(lowestFoundRock + 1);
                    newMovingRocks.get(x).add(lowestFoundRock + 1);
                }
            }

            movingRocks = newMovingRocks;
        }

        private void tiltEast() {
            List<List<Integer>> eastGravityColumns = initializeListOfListOfInt(height);

            for (int x = 0; x < height; x++) {
                for (int y : fixedRocks.get(x)) {
                    eastGravityColumns.get(x).add(y);
                }
            }

            List<List<Integer>> newMovingRocks = initializeListOfListOfInt(height);
            for (int x = 0; x < height; x++) {
                Collections.sort(movingRocks.get(x));
                Collections.reverse(movingRocks.get(x));
                for (int y : movingRocks.get(x)) {
                    int lowestFoundRock = eastGravityColumns.get(x)
                            .stream()
                            .filter(rockY -> rockY > y)
                            .min(Integer::compareTo)
                            .orElse(width);
                    eastGravityColumns.get(x).add(lowestFoundRock - 1);
                    newMovingRocks.get(x).add(lowestFoundRock - 1);
                }
            }

            movingRocks = newMovingRocks;
        }
    }
}