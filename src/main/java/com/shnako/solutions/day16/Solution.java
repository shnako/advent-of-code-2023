package com.shnako.solutions.day16;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.shnako.solutions.day16.Solution.Direction.*;

/*
We store the directions we can go in at each point, based on the current tile and incoming direction in BEAM_DIRECTIONS.
We use the above to simulate the beam movements using a DFS algorithm.
There are cycles in the layouts, so we keep a list of beams we've already seen to detect cycles.
The number of energised tiles is calculated by extracting the unique tiles crossed by beams.

Part 1:
We start from the tile at location [-1, 0] moving east.
The result is the number of energised tiles.

Part 2:
We generate a list of beams from all tiles around the layout moving inward and calculate the number of energised tiles.
The result is the maximum number of energised tiles.
 */
public class Solution extends SolutionBase {
    private Set<Beam> beamMoves;

    private static final Map<Character, Map<Direction, List<Direction>>> BEAM_DIRECTIONS = Map.of(
            '.', Map.of(
                    N, List.of(N),
                    W, List.of(W),
                    S, List.of(S),
                    E, List.of(E)),
            '/', Map.of(
                    N, List.of(E),
                    W, List.of(S),
                    S, List.of(W),
                    E, List.of(N)),
            '\\', Map.of(
                    N, List.of(W),
                    W, List.of(N),
                    S, List.of(E),
                    E, List.of(S)),
            '-', Map.of(
                    N, List.of(W, E),
                    W, List.of(W),
                    S, List.of(W, E),
                    E, List.of(E)),
            '|', Map.of(
                    N, List.of(N),
                    W, List.of(N, S),
                    S, List.of(S),
                    E, List.of(N, S)
            )
    );

    @Override
    public String runPart1() throws IOException {
        char[][] layout = InputProcessingUtil.readCharGrid(getDay());
        Beam beam = new Beam(0, -1, E);
        int result = findEnergisedTiles(layout, beam);
        return String.valueOf(result);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private int findEnergisedTiles(char[][] layout, Beam beam) {
        beamMoves = new HashSet<>();
        move(layout, beam);
        return beamMoves.stream()
                .map(bm -> Pair.of(bm.x, bm.y))
                .collect(Collectors.toSet())
                .size();
    }

    private void move(char[][] layout, Beam beam) {
        int nextX = beam.x + beam.direction.x;
        int nextY = beam.y + beam.direction.y;
        if (nextX < 0 || nextY < 0 || nextX >= layout.length || nextY >= layout[0].length) {
            return;
        }

        for (Direction nextDirection : BEAM_DIRECTIONS.get(layout[nextX][nextY]).get(beam.direction)) {
            Beam nextBeam = new Beam(nextX, nextY, nextDirection);
            if (!beamMoves.contains(nextBeam)) {
                beamMoves.add(nextBeam);
                move(layout, nextBeam);
            }
        }
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String runPart2() throws IOException {
        char[][] layout = InputProcessingUtil.readCharGrid(getDay());
        int result = getStartingBeams(layout)
                .stream()
                .mapToInt(beam -> findEnergisedTiles(layout, beam))
                .max()
                .getAsInt();
        return String.valueOf(result);
    }

    private List<Beam> getStartingBeams(char[][] layout) {
        List<Beam> result = new ArrayList<>();
        for (int i = 0; i < layout.length; i++) {
            result.add(new Beam(i, -1, E));
            result.add(new Beam(i, layout[i].length, W));
        }
        for (int i = 0; i < layout[0].length; i++) {
            result.add(new Beam(-1, i, S));
            result.add(new Beam(layout.length, i, N));
        }
        return result;
    }

    private record Beam(int x, int y, Direction direction) {
    }

    enum Direction {
        // x and y represent the coordinate deltas when moving in each direction.
        N(-1, 0),
        W(0, -1),
        S(1, 0),
        E(0, 1);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}