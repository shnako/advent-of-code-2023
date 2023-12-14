package com.shnako.solutions.day14;

import com.google.common.collect.Streams;
import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        Platform platform = new Platform(input);
        platform.tiltNorth();
        return String.valueOf(platform.calculateLoad());
    }

    @Override
    public String runPart2() {
        return "456";
    }

    private List<List<Integer>> initializeListOfListOfInt(int size) {
        List<List<Integer>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new ArrayList<>());
        }
        return result;
    }

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
                        case '#':
                            fixedRocks.get(i).add(j);
                            break;
                        case 'O':
                            movingRocks.get(i).add(j);
                            break;
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

        // 0 - N | 1 - W | 2 - S | 3 - E
        private void tilt(int direction) {
            switch(direction) {
                case 0: tiltNorth();
//                case 1: tiltSouth();
            }
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
                for (int y : movingRocks.get(x)) {
                    int finalX = x;
                    int highestFoundRock = Streams.findLast(northGravityColumns.get(y).stream()
                                    .filter(rockX -> rockX < finalX))
                            .orElse(-1);
                    northGravityColumns.get(y).add(highestFoundRock + 1);
                    newMovingRocks.get(highestFoundRock + 1).add(y);
                    Collections.sort(northGravityColumns.get(y));
                }
            }

            movingRocks = newMovingRocks;
        }
    }
}