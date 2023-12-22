package com.shnako.solutions.day22;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.reverseOrder;

/*
We parse bricks as Brick objects, which get sorted by their lowest z coordinate.
We determine which bricks a brick can stack with and store the ones above and ones below in the Brick object, sorted.
We then let the bricks fall until they hit the ground or another brick, going from lowest to highest.

Part 1:
For each brick, we mark it as unsafe if any of the bricks above it are only supported by it.
The result is the number of bricks not marked as unsafe.

Part 2:
For each brick, we use a BFS algorithm to simulate which bricks would fall if it were removed.
We keep a queue of impacted bricks that we need to verify and a set of bricks that have already fallen.
As we go up the brick stack, we check if all of a brick's supporting bricks have fallen,
and if so, we mark it as fallen and all the bricks above it as impacted,
and continue until we run out of impacted bricks.
The result is the sum of fallen bricks on each removed brick.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Brick> bricks = parseInput();
        stackBricks(bricks);
        letGravityDoItsThing(bricks);

        int result = 0;
        for (Brick brick : bricks) {
            boolean isSafeToDisintegrate = true;
            for (Brick brickAbove : brick.bricksAbove) {
                if (brickAbove.bricksBelow.stream().noneMatch(bb -> bb != brick && bb.to.z + 1 == brickAbove.from.z)) {
                    isSafeToDisintegrate = false;
                    break;
                }
            }
            if (isSafeToDisintegrate) {
                result++;
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        List<Brick> bricks = parseInput();
        stackBricks(bricks);
        letGravityDoItsThing(bricks);

        int result = 0;
        Queue<Brick> impactedBricks = new LinkedList<>();
        for (Brick brick : bricks) {
            Set<Brick> fallenBricks = new HashSet<>();
            impactedBricks.add(brick);
            fallenBricks.add(brick);
            while (!impactedBricks.isEmpty()) {
                Brick fallingBrick = impactedBricks.poll();
                for (Brick brickAbove : fallingBrick.bricksAbove) {
                    if (!fallenBricks.contains(brickAbove) && isBrickFalling(brickAbove, fallenBricks)) {
                        impactedBricks.add(brickAbove);
                        fallenBricks.add(brickAbove);
                    }
                }
            }
            result += fallenBricks.size() - 1;
        }

        return String.valueOf(result);
    }

    private boolean isBrickFalling(Brick brick, Set<Brick> fallenBricks) {
        for (Brick brickBelow : brick.bricksBelow) {
            if (fallenBricks.contains(brickBelow)) {
                continue;
            }
            if (brickBelow.to.z + 1 == brick.from.z) {
                return false;
            }
        }
        return true;
    }

    private void stackBricks(List<Brick> bricks) {
        for (int i = 0; i < bricks.size(); i++) {
            for (int j = i + 1; j < bricks.size(); j++) {
                if (areBricksStacking(bricks.get(i), bricks.get(j))) {
                    bricks.get(i).addStackingBrick(bricks.get(j));
                    bricks.get(j).addStackingBrick(bricks.get(i));
                }
            }
        }
    }

    private boolean areBricksStacking(Brick b1, Brick b2) {
        return are2DCoordinatesOverlapping(b1.from.x, b1.to.x, b2.from.x, b2.to.x)
                && are2DCoordinatesOverlapping(b1.from.y, b1.to.y, b2.from.y, b2.to.y);
    }

    private boolean are2DCoordinatesOverlapping(int from1, int to1, int from2, int to2) {
        return to1 >= from2 && to2 >= from1;
    }

    private void letGravityDoItsThing(List<Brick> bricks) {
        Collections.sort(bricks);
        for (Brick brick : bricks) {
            int deltaZ = brick.from.z - 1;
            if (!brick.bricksBelow.isEmpty()) {
                deltaZ -= brick.bricksBelow.stream().mapToInt(x -> x.to.z).max().getAsInt();
            }
            brick.moveDown(deltaZ);
        }
    }

    private List<Brick> parseInput() throws IOException {
        return new ArrayList<>(InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(this::parseLine)
                .toList());
    }

    private Brick parseLine(String line) {
        String[] components = line.split("~");

        String[] c1 = components[0].split(",");
        Coordinate a = new Coordinate(Integer.parseInt(c1[0]), Integer.parseInt(c1[1]), Integer.parseInt(c1[2]));
        String[] c2 = components[1].split(",");
        Coordinate b = new Coordinate(Integer.parseInt(c2[0]), Integer.parseInt(c2[1]), Integer.parseInt(c2[2]));

        return new Brick(a, b);
    }

    private static class Brick implements Comparable<Brick> {
        private final Coordinate from, to;
        private final List<Brick> bricksAbove, bricksBelow;

        private Brick(Coordinate from, Coordinate to) {
            this.from = from;
            this.to = to;

            bricksAbove = new ArrayList<>();
            bricksBelow = new ArrayList<>();
        }

        private void moveDown(int spaces) {
            from.z -= spaces;
            to.z -= spaces;
        }

        private void addStackingBrick(Brick brick) {
            if (this.compareTo(brick) < 0) {
                bricksAbove.add(brick);
                Collections.sort(bricksAbove);
            } else {
                bricksBelow.add(brick);
                bricksBelow.sort(reverseOrder());
            }
        }

        @Override
        public int compareTo(@NotNull Brick o) {
            return this.from.z - o.from.z;
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", from, to);
        }
    }

    private static class Coordinate {
        private final int x;
        private final int y;
        private int z;

        private Coordinate(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return String.format("%d, %d, %d", x, y, z);
        }
    }
}