package com.shnako.solutions.day21;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
We store the grid as a boolean matrix to simplify operations, with # being represented as true, and . and S as false.

Part 1:
We use a BFS algorithm to move from the starting point outwards the specified number of steps,
and store the coordinates we have reached in a set to avoid duplicates.
The result is the size of this set.

Part 2:
This was an absolute pain, more like walking through the dark and eventually finding the door than coding...
My first idea was to find cycles in the map and this was a real rabbit hole as there are rhombus shapes in the input.
I realised that it would be far too complicated to actually simulate what happens on all the edges so gave up on that.
I figured the solution must be something mathematical rather than algorithmic, and I looked deeper into the examples.
I thought there might be some sort of formula going from the number of steps to the result, so I focused on that.
It clearly couldn't have been linear so decided to try using Wolfram Alpha to look for a more complex one,
and sure enough it turned out there could be a quadratic one: https://www.wolframalpha.com/input?i=quadratic+fit+calculator&assumption=%7B%22F%22%2C+%22QuadraticFitCalculator%22%2C+%22data3x%22%7D+-%3E%22%7B6%2C+10%2C+50%2C++100%2C+500%2C+1000%2C+5000%7D%22&assumption=%7B%22F%22%2C+%22QuadraticFitCalculator%22%2C+%22data3y%22%7D+-%3E%22%7B16%2C+50%2C+1594%2C+6536%2C+167004%2C+668697%2C+16733044%7D%22
I tried to simulate some random steps using the part 1 implementation on a larger grid and see if I could get a result,
and I managed to get a too low and a too high result with the same number of digits, so I knew I was close.
I then figured 26501365 must have some special properties but sadly not much came out of it.
I figured the size of the input must have some part in it as well so started playing around with 131.
I noticed that dividing 26501365 by 131 is 2023 * 100 + a remainder,
so took the 2023 in there as a hint that I'm on the right path.
I figured it must be something to do with the remainder (65) as well,
so I decided to try generating a quadratic formula for various combinations of the numbers
and see if I can get a result in the too low / high range I had from the previous tried.
I noticed that using 65, 65 + 131 and 65 + 131 * 2 generated a number in my range so tried it and it worked!
The successful formula generation is here: https://www.wolframalpha.com/input?i=quadratic+fit+%7B%7B65%2C+3917%7D%2C+%7B196%2C+34920%7D%2C+%7B327%2C+96829%7D%7D
The result calculation is here: https://www.wolframalpha.com/input?i2d=true&i=x+%3D+26501365+for+Divide%5B15453+Power%5Bx%2C2%5D%2C17161%5D%2BDivide%5B28160x%2C17161%5D%2BDivide%5B100312%2C17161%5D
I was initially trying a much larger grid, but turns out it works with a grid enlarged 5 times as well.
 */
@SuppressWarnings("ExtractMethodRecommender")
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        Garden garden = parseInput();
        int result = findOptionsForSteps(garden, 64);
        return String.valueOf(result);
    }

    private int findOptionsForSteps(Garden garden, int steps) {
        Set<Coordinate> currentCoordinates = new HashSet<>();
        Set<Coordinate> newCoordinates;
        currentCoordinates.add(garden.start);

        for (int step = 0; step < steps; step++) {
            newCoordinates = new HashSet<>();
            for (Coordinate coordinate : currentCoordinates) {
                newCoordinates.addAll(getValidMoves(garden, coordinate));
            }
            currentCoordinates = newCoordinates;
        }
        return currentCoordinates.size();
    }

    private static final List<Coordinate> MOVES = List.of(
            new Coordinate(-1, 0),
            new Coordinate(0, 1),
            new Coordinate(1, 0),
            new Coordinate(0, -1)
    );

    private List<Coordinate> getValidMoves(Garden garden, Coordinate coordinate) {
        return MOVES
                .stream()
                .map(move -> new Coordinate(coordinate.r + move.r, coordinate.c + move.c))
                .filter(nextCoordinate -> !isOutOfBounds(garden, nextCoordinate))
                .filter(nextCoordinate -> !isRock(garden, nextCoordinate))
                .collect(Collectors.toList());
    }

    private boolean isOutOfBounds(Garden garden, Coordinate coordinate) {
        return coordinate.r < 0 || coordinate.r > garden.grid.length
                || coordinate.c < 0 || coordinate.c > garden.grid[0].length;
    }

    private boolean isRock(Garden garden, Coordinate coordinate) {
        return garden.grid[coordinate.r][coordinate.c];
    }

    @Override
    public String runPart2() throws IOException {
        final int steps = 26501365;
        Garden garden = parseInput();
        int gridSize = garden.grid.length;
        int remainder = steps % gridSize;
        List<Integer> stepsToCheck = List.of(remainder, remainder + gridSize, remainder + gridSize * 2);
        Garden bigGarden = garden.enlarge(5);
        String stepResults = stepsToCheck
                .stream()
                .map(x -> Pair.of(x, findOptionsForSteps(bigGarden, x)))
                .map(pair -> String.format("{%d, %d}", pair.getLeft(), pair.getRight()))
                .collect(Collectors.joining(", "));

        String pasteIntoWolframAlpha = String.format("quadratic fit {%s}", stepResults);
        System.out.println("Paste this into wolframalpha.com: " + pasteIntoWolframAlpha);

        // Wolfram Alpha returns: 15453 * x * x / 17161 + 28160 * x / 17161 + 100312 / 17161
        // Which can be simplified to (15453 * x * x + 28160 * x + 100312) / 17161
        BigInteger bigSteps = BigInteger.valueOf(steps);
        BigInteger result = BigInteger
                .valueOf(15453)
                .multiply(bigSteps)
                .multiply(bigSteps)
                .add(BigInteger.valueOf(28160).multiply(bigSteps))
                .add(BigInteger.valueOf(100312))
                .divide(BigInteger.valueOf(17161));
        return String.valueOf(result);
    }

    private Garden parseInput() throws IOException {
        char[][] input = InputProcessingUtil.readCharGrid(getDay());
        boolean[][] boolGrid = new boolean[input.length][input[0].length];
        int startR = -1, startC = -1;
        for (int r = 0; r < input.length; r++) {
            for (int c = 0; c < input[r].length; c++) {
                boolGrid[r][c] = input[r][c] == '#';
                if (input[r][c] == 'S') {
                    startR = r;
                    startC = c;
                }
            }
        }
        return new Garden(boolGrid, new Coordinate(startR, startC));
    }

    @SuppressWarnings("SameParameterValue")
    private record Garden(boolean[][] grid, Coordinate start) {
        private Garden enlarge(int times) {
            if (times % 2 == 0) {
                throw new RuntimeException("Enlarging even number of times is not supported");
            }

            boolean[][] resultGrid = new boolean[grid.length * times][grid[0].length * times];
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    for (int timeR = 0; timeR < times; timeR++) {
                        for (int timeC = 0; timeC < times; timeC++) {
                            resultGrid[grid.length * timeR + r][grid[r].length * timeC + c] = grid[r][c];
                        }
                    }
                }
            }
            int newStartR = grid.length * (times / 2) + start.r;
            int newStartC = grid[0].length * (times / 2) + start.c;
            return new Garden(resultGrid, new Coordinate(newStartR, newStartC));
        }
    }

    private record Coordinate(int r, int c) {
    }
}