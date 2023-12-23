package com.shnako.solutions.day23;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
The solution is based on the observation that the input is really a graph with each node being a crossroad.
We therefore start by processing the input into a graph starting from the entrance and ending at the exit.
In the Crossroad object, we store the distance to each neighbouring crossroads.

Part 1:
We build the graph taking in consideration the slope rules.
The maximum distance is calculated using a DFS algorithm that looks at all possible routes.

Part 2:
We replace all the slopes with paths in the input and build the graph.
This opens up a lot more routes, but the same algorithm still works fast enough.
The maximum distance is calculated using a DFS algorithm that looks at all possible routes.

"Fun" fact: < and ^ do not appear in the input or example, even though they're mentioned in the puzzle.
 */
public class Solution extends SolutionBase {
    private static final Map<Character, List<Coordinates>> MOVES = Map.of(
            '^', List.of(new Coordinates(-1, 0)),
            '>', List.of(new Coordinates(0, 1)),
            'v', List.of(new Coordinates(1, 0)),
            '<', List.of(new Coordinates(0, -1)),
            '.', List.of(
                    new Coordinates(-1, 0),
                    new Coordinates(0, 1),
                    new Coordinates(1, 0),
                    new Coordinates(0, -1)
            )
    );

    @Override
    public String runPart1() throws IOException {
        char[][] grid = InputProcessingUtil.readCharGrid(getDay());
        return solve(grid);
    }

    @Override
    public String runPart2() throws IOException {
        char[][] grid = InputProcessingUtil.readCharGrid(getDay());

        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] != '#') {
                    grid[r][c] = '.';
                }
            }
        }

        return solve(grid);
    }

    private String solve(char[][] grid) {
        Coordinates from = findEntrance(grid, 0);
        Coordinates to = findEntrance(grid, grid.length - 1);
        Map<Coordinates, Crossroad> crossroadMap = getCrossroadGraphMap(grid, from, to);
        int result = findLongestPath(crossroadMap.get(from), crossroadMap.get(to), 0, List.of(), crossroadMap);
        return String.valueOf(result);
    }

    private Coordinates findEntrance(char[][] grid, int row) {
        for (int c = 0; c < grid[row].length; c++) {
            if (grid[row][c] == '.') {
                return new Coordinates(row, c);
            }
        }
        throw new RuntimeException("Exit not found on row " + row);
    }

    private Map<Coordinates, Crossroad> getCrossroadGraphMap(char[][] grid, Coordinates startCoordinates, Coordinates endCoordinates) {
        Map<Coordinates, Crossroad> crossroadMap = new HashMap<>();
        Queue<Crossroad> crossRoadQueue = new LinkedList<>();
        Crossroad start = new Crossroad(startCoordinates);
        crossRoadQueue.add(start);
        while (!crossRoadQueue.isEmpty()) {
            Crossroad crossroad = crossRoadQueue.poll();
            if (crossroadMap.containsKey(crossroad.coordinates)) {
                continue;
            }
            List<Coordinates> neighbours = getValidNeighbours(crossroad.coordinates, grid, null);
            for (Coordinates neighbour : neighbours) {
                Pair<Coordinates, Integer> neighbourCrossroadDistance =
                        findClosestCrossroad(crossroad.coordinates, neighbour, grid, endCoordinates);
                if (neighbourCrossroadDistance == null) {
                    continue;
                }
                Crossroad neighbourCrossroad = crossroadMap.getOrDefault(neighbourCrossroadDistance.getLeft(),
                        new Crossroad(neighbourCrossroadDistance.getLeft()));
                int distance = neighbourCrossroadDistance.getRight();

                if (!crossroad.neighbourDistances.containsKey(neighbourCrossroad.coordinates)
                        || crossroad.neighbourDistances.get(neighbourCrossroad.coordinates) < distance) {
                    crossroad.neighbourDistances.put(neighbourCrossroad.coordinates, distance);
                    crossRoadQueue.add(neighbourCrossroad);
                }
            }
            crossroadMap.put(crossroad.coordinates, crossroad);
        }
        return crossroadMap;
    }

    private Pair<Coordinates, Integer> findClosestCrossroad(Coordinates from, Coordinates neighbour, char[][] grid, Coordinates endCoordinates) {
        int distance = 1;
        Coordinates current = neighbour, incoming = from;
        while (true) {
            if (current.equals(endCoordinates)) {
                return Pair.of(current, distance);
            }
            List<Coordinates> nextCoordinates = getValidNeighbours(current, grid, incoming);
            if (nextCoordinates.isEmpty()) {
                return null;
            }
            if (nextCoordinates.size() > 1) {
                return Pair.of(current, distance);
            }

            incoming = current;
            current = nextCoordinates.get(0);
            distance++;
        }
    }

    private List<Coordinates> getValidNeighbours(Coordinates coordinates, char[][] grid, Coordinates incoming) {
        return MOVES.get(grid[coordinates.r][coordinates.c])
                .stream()
                .map(move -> new Coordinates(coordinates.r + move.r, coordinates.c + move.c))
                .filter(move -> !move.equals(incoming))
                .filter(move -> move.r >= 0 && move.r < grid.length && move.c >= 0 && move.c < grid[coordinates.r].length)
                .filter(move -> grid[move.r][move.c] != '#')
                .filter(move -> !isUphill(move, coordinates, grid))
                .collect(Collectors.toList());
    }

    private boolean isUphill(Coordinates to, Coordinates from, char[][] grid) {
        if (grid[to.r][to.c] == '.') {
            return false;
        }
        Coordinates allowedMove = MOVES.get(grid[to.r][to.c]).get(0);
        Coordinates move = new Coordinates(to.r - from.r, to.c - from.c);

        return allowedMove.r + move.r == 0 && allowedMove.c + move.c == 0;
    }

    private int findLongestPath(Crossroad current, Crossroad to, int distance, List<Coordinates> path, Map<Coordinates, Crossroad> crossroadMap) {
        if (current.equals(to)) {
            return distance;
        }

        path = new ArrayList<>(path);
        path.add(current.coordinates);
        List<Coordinates> finalPath = path;
        return current.neighbourDistances.entrySet()
                .stream()
                .filter(e -> !finalPath.contains(e.getKey()))
                .mapToInt(e -> findLongestPath(crossroadMap.get(e.getKey()), to, distance + e.getValue(), finalPath, crossroadMap))
                .max()
                .orElse(0);
    }

    private record Coordinates(int r, int c) {
    }

    private static class Crossroad {
        private final Coordinates coordinates;
        private final Map<Coordinates, Integer> neighbourDistances;

        private Crossroad(Coordinates coordinates) {
            this.coordinates = coordinates;
            this.neighbourDistances = new HashMap<>();
        }

        @Override
        public String toString() {
            return String.format("[%d, %d] -> %d", coordinates.r, coordinates.c, neighbourDistances.size());
        }
    }
}