package com.shnako.solutions.day10;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
Because the pipe is a closed loop, we don't need to use fancy algorithms like BFS or DFS to find the loop.
We simply follow the pipe from the start position in either direction until we reach the start position again.
We keep the loop as a PipeLoop object, which is optimised for the operations we need in both parts.

Part 1:
Because this is a closed loop, we know that the furthest part of the pipe is at the other side of the loop start.
The result is therefore half the pipe loop's distance.

Part 2:
The solution for this uses a ray casting algorithm: https://en.wikipedia.org/wiki/Point_in_polygon
This is based on the observation that if a point is inside the pipe area,
it will have an odd number of pipe components in any direction.
We therefore look to the left of each point on the map that is not part of the pipe
and count the number of pipe components we find before reaching the edge of the map.
One complication here is that if the pipe extends at all to the point's left then this will not work correctly.
We therefore only count angled pipe components where the pipe goes up and not when it comes down.
This surfaces another complication - the starting point needs to be replaced with the correct pipe component in the map.
The result is the number of points that have an odd number of pipe components to their left.
 */
public class Solution extends SolutionBase {
    private static final Map<Character, int[][]> FLOW_MAP = Map.of(
            '.', new int[][]{},
            'F', new int[][]{{1, 0}, {0, 1}},
            '-', new int[][]{{0, -1}, {0, 1}},
            '7', new int[][]{{0, -1}, {1, 0}},
            '|', new int[][]{{-1, 0}, {1, 0}},
            'J', new int[][]{{-1, 0}, {0, -1}},
            'L', new int[][]{{0, 1}, {-1, 0}},
            'S', new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}
    );

    private static final List<Character> LEFT_PIPE_SEGMENTS = List.of('L', '|', 'J');

    @Override
    public String runPart1() throws IOException {
        char[][] map = InputProcessingUtil.readCharGrid(getDay());
        int[] startLocation = findStart(map);
        PipeLoop pipeLoop = findPipeLoop(map, startLocation);
        int result = Math.ceilDiv(pipeLoop.getSize(), 2);
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        char[][] map = InputProcessingUtil.readCharGrid(getDay());
        int[] startLocation = findStart(map);
        PipeLoop pipeLoop = findPipeLoop(map, startLocation);
        replaceStartWithPipeSegment(map, pipeLoop);

        int tilesInsideLoop = 0;
        for (int i = 1; i < map.length; i++) {
            for (int j = 1; j < map[i].length; j++) {
                if (pipeLoop.isPartOfPipe(i, j)) {
                    continue;
                }

                int segments = getPipeSegmentsToTheLeft(map, pipeLoop, i, j);
                if (segments % 2 == 1) {
                    tilesInsideLoop++;
                }
            }
        }

        return String.valueOf(tilesInsideLoop);
    }

    private int[] findStart(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 'S') {
                    return new int[]{i, j};
                }
            }
        }
        throw new RuntimeException("Could not find start coordinates.");
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void replaceStartWithPipeSegment(char[][] map, PipeLoop pipeLoop) {
        int[] source = pipeLoop.pipeLoopList.get(0);
        int[] neighbour1 = pipeLoop.pipeLoopList.get(1);
        int[] neighbour2 = pipeLoop.pipeLoopList.get(pipeLoop.getSize() - 1);

        int[] sToN1 = new int[]{neighbour1[0] - source[0], neighbour1[1] - source[1]};
        int[] sToN2 = new int[]{neighbour2[0] - source[0], neighbour2[1] - source[1]};

        char pipeSegment = FLOW_MAP.entrySet()
                .stream()
                .filter(e -> e.getKey() != 'S')
                .filter(e -> supportsFlowDirection(e.getValue(), sToN1[0], sToN1[1]))
                .filter(e -> supportsFlowDirection(e.getValue(), sToN2[0], sToN2[1]))
                .map(Map.Entry::getKey)
                .findAny()
                .get();

        map[source[0]][source[1]] = pipeSegment;
    }

    private PipeLoop findPipeLoop(char[][] map, int[] start) {
        List<int[]> pipeLoop = new ArrayList<>();
        int[] current = start;
        int[] flowDirection = null;
        do {
            pipeLoop.add(current);
            int[] flowIncomingDirection = flowDirection == null ? null : new int[]{-flowDirection[0], -flowDirection[1]};
            for (int[] flow : FLOW_MAP.get(map[current[0]][current[1]])) {
                if (flowIncomingDirection != null
                        && flow[0] == flowIncomingDirection[0] && flow[1] == flowIncomingDirection[1]) {
                    continue;
                }
                int[] next = new int[]{current[0] + flow[0], current[1] + flow[1]};
                if (isNotOutOfBounds(next, map) && arePipeSegmentsConnected(map, current, next)) {
                    flowDirection = flow;
                    current = next;
                    break;
                }
            }
        } while (!Arrays.equals(current, start));
        return new PipeLoop(pipeLoop, map.length, map[0].length);
    }

    private boolean isNotOutOfBounds(int[] next, char[][] map) {
        return next[0] >= 0 && next[1] >= 0 && next[0] < map.length && next[1] < map[next[0]].length;
    }

    private boolean arePipeSegmentsConnected(char[][] map, int[] s1, int[] s2) {
        int[] s1ToS2 = new int[]{s2[0] - s1[0], s2[1] - s1[1]};
        int[] s2ToS1 = new int[]{s1[0] - s2[0], s1[1] - s2[1]};
        return supportsFlow(map[s1[0]][s1[1]], s1ToS2) && supportsFlow(map[s2[0]][s2[1]], s2ToS1);
    }

    private boolean supportsFlow(char c, int[] flow) {
        return supportsFlowDirection(FLOW_MAP.get(c), flow[0], flow[1]);
    }

    private boolean supportsFlowDirection(int[][] list, int x, int y) {
        for (int[] possiblePosition : list) {
            if (possiblePosition[0] == x && possiblePosition[1] == y) {
                return true;
            }
        }
        return false;
    }

    private int getPipeSegmentsToTheLeft(char[][] map, PipeLoop pipeLoop, int x, int y) {
        int pipeSegmentsToTheLeft = 0;
        for (int j = y; j >= 0; j--) {
            if (LEFT_PIPE_SEGMENTS.contains(map[x][j]) && pipeLoop.isPartOfPipe(x, j)) {
                pipeSegmentsToTheLeft++;
            }
        }
        return pipeSegmentsToTheLeft;
    }

    private static class PipeLoop {
        private final List<int[]> pipeLoopList;
        private final int[][] pipeLoopArray;

        private PipeLoop(List<int[]> pipeLoopList, int rows, int columns) {
            this.pipeLoopList = pipeLoopList;
            this.pipeLoopArray = new int[rows][columns];
            pipeLoopList.forEach(coordinates -> pipeLoopArray[coordinates[0]][coordinates[1]] = 1);
        }

        private boolean isPartOfPipe(int x, int y) {
            return pipeLoopArray[x][y] != 0;
        }

        private int getSize() {
            return pipeLoopList.size();
        }
    }
}