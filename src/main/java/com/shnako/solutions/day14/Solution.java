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
        List<List<Integer>> northGravityRockList = getNorthGravityColumnMap(platform);

        int result = 0;
        for (int y = 0; y < platform.height; y++) {
            for (int x : northGravityRockList.get(y)) {
                if (!platform.fixedRocks.get(x).contains(y)) {
                    result += platform.height - x;
                }
            }
        }

        return String.valueOf(result);
    }

    private List<List<Integer>> getNorthGravityColumnMap(Platform platform) {
        List<List<Integer>> result = initializeListOfListOfInt(platform.width);

        for (int x = 0; x < platform.height; x++) {
            for (int y : platform.fixedRocks.get(x)) {
                result.get(y).add(x);
            }
        }

        for (int x = 0; x < platform.height; x++) {
            for (int y : platform.movingRocks.get(x)) {
                int finalX = x;
                int highestFoundRock = Streams.findLast(result.get(y).stream()
                                .filter(rockX -> rockX < finalX))
                                .orElse(-1);
                result.get(y).add(highestFoundRock + 1);
                Collections.sort(result.get(y));
            }
        }

        return result;
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
        private final List<List<Integer>> movingRocks;

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
    }
}