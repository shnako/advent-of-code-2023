package com.shnako.solutions.day24;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/*
This was by far the worst challenge I've seen this year. It was purely mathematical, nothing fun about it.
I have decided to not refactor anything here, in the hope that it might emphasize how painful this was.
Oh, and also because it's Christmas Eve, and I've been dealing with this all day instead of spending time with family!

Part 1:
This was basically a problem of finding the intersection of 2 lines in 2D space using algebra.
Code for this was inspired by this: https://paulbourke.net/geometry/pointlineplane/#i2l
The result is the number of intersections that occur in the specified area in the future.

Part 2:
This is far beyond my mathematical knowledge and interest,
and because there was no decent algorithmic implementation possible,
I decided to implement a mathematical solution based on a post from Reddit.
I have a general idea of how and why it works, but I'll let the original comment describe it (screenshot in folder too).
https://www.reddit.com/r/adventofcode/comments/18pnycy/comment/keqf8uq/
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Hailstone> hailstones = parseInput();
        List<Coordinates> intersections = findHailstoneIntersections(hailstones);

        TestArea testArea = new TestArea(new BigDecimal("200000000000000"), new BigDecimal("200000000000000"),
                new BigDecimal("400000000000000"), new BigDecimal("400000000000000"));

        long result = intersections
                .stream()
                .filter(intersection -> isIn2DTestArea(intersection, testArea))
                .count();

        return String.valueOf(result);
    }

    private boolean isIn2DTestArea(Coordinates c, TestArea testArea) {
        return c.x.compareTo(testArea.x1) >= 0 && c.x.compareTo(testArea.x2) <= 0
                && c.y.compareTo(testArea.y1) >= 0 && c.y.compareTo(testArea.y2) <= 0;
    }

    private List<Coordinates> findHailstoneIntersections(List<Hailstone> hailstones) {
        List<Coordinates> result = new ArrayList<>();
        for (int i = 0; i < hailstones.size(); i++) {
            for (int j = i + 1; j < hailstones.size(); j++) {
                Hailstone h1 = hailstones.get(i);
                Hailstone h2 = hailstones.get(j);
                Coordinates intersection = find2DIntersection(h1, h2);
                if (intersection != null && !h1.is2DPointInPast(intersection) && !h2.is2DPointInPast(intersection)) {
                    result.add(intersection);
                }
            }
        }
        return result;
    }

    // Based on: https://paulbourke.net/geometry/pointlineplane/#i2l
    @SuppressWarnings({"DuplicatedCode", "DuplicateExpressions"})
    private Coordinates find2DIntersection(Hailstone h1, Hailstone h2) {
        BigDecimal denominator1 = (h2.b.y.subtract(h2.a.y)).multiply(h1.b.x.subtract(h1.a.x));
        BigDecimal denominator2 = (h2.b.x.subtract(h2.a.x)).multiply(h1.b.y.subtract(h1.a.y));
        BigDecimal denominator = denominator1.subtract(denominator2);

        if (denominator.equals(BigDecimal.ZERO)) {
            return null; // Parallel lines
        }

        BigDecimal uA1 = (h2.b.x.subtract(h2.a.x)).multiply(h1.a.y.subtract(h2.a.y));
        BigDecimal uA2 = (h2.b.y.subtract(h2.a.y)).multiply(h1.a.x.subtract(h2.a.x));
        BigDecimal uA = (uA1.subtract(uA2)).divide(denominator, 3, RoundingMode.DOWN);

        BigDecimal intersectionX = h1.a.x.add(uA.multiply(h1.b.x.subtract(h1.a.x)));
        BigDecimal intersectionY = h1.a.y.add(uA.multiply(h1.b.y.subtract(h1.a.y)));

        return new Coordinates(intersectionX, intersectionY, BigDecimal.ZERO);
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "DataFlowIssue", "DuplicatedCode"})
    @Override
    public String runPart2() throws IOException {
        List<Hailstone> hailstones = parseInput();

        Map<Integer, Integer> xFrequencies = new HashMap<>();
        for (Hailstone hailstone : hailstones) {
            xFrequencies.put(hailstone.velocity.x.intValue(), xFrequencies.getOrDefault(hailstone.velocity.x.intValue(), 0) + 1);
        }

        Map<Integer, Integer> yFrequencies = new HashMap<>();
        for (Hailstone hailstone : hailstones) {
            yFrequencies.put(hailstone.velocity.y.intValue(), yFrequencies.getOrDefault(hailstone.velocity.y.intValue(), 0) + 1);
        }

        Map<Integer, Integer> zFrequencies = new HashMap<>();
        for (Hailstone hailstone : hailstones) {
            zFrequencies.put(hailstone.velocity.z.intValue(), zFrequencies.getOrDefault(hailstone.velocity.z.intValue(), 0) + 1);
        }

        List<Integer> commonXSpeeds = xFrequencies.entrySet()
                .stream()
                .filter(xSpeed -> xSpeed.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        List<Integer> commonYSpeeds = yFrequencies.entrySet()
                .stream()
                .filter(ySpeed -> ySpeed.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        List<Integer> commonZSpeeds = zFrequencies.entrySet()
                .stream()
                .filter(zSpeed -> zSpeed.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        Set<Integer> possibleXSpeeds = new HashSet<>();
        Set<Integer> possibleYSpeeds = new HashSet<>();
        Set<Integer> possibleZSpeeds = new HashSet<>();

        for (int xSpeed : commonXSpeeds) {
            List<Hailstone> stonesWithXSpeed = hailstones
                    .stream()
                    .filter(hs -> hs.velocity.x.intValue() == xSpeed)
                    .toList();
            for (int i = 0; i < stonesWithXSpeed.size(); i++) {
                for (int j = i + 1; j < stonesWithXSpeed.size(); j++) {
                    Set<Integer> validSpeeds = new HashSet<>();
                    BigDecimal xDifference = stonesWithXSpeed.get(1).a.x.subtract(stonesWithXSpeed.get(0).a.x);
                    for (int velocity = -1000; velocity < 1000; velocity++) {
                        if (xSpeed != velocity && xDifference.remainder(new BigDecimal(xSpeed).subtract(new BigDecimal(velocity))).equals(BigDecimal.ZERO)) {
                            validSpeeds.add(velocity);
                        }
                    }
                    if (possibleXSpeeds.isEmpty()) {
                        possibleXSpeeds = validSpeeds;
                    } else {
                        possibleXSpeeds.retainAll(validSpeeds);
                    }
                }
            }
        }

        for (int ySpeed : commonYSpeeds) {
            List<Hailstone> stonesWithYSpeed = hailstones
                    .stream()
                    .filter(hs -> hs.velocity.y.intValue() == ySpeed)
                    .toList();
            for (int i = 0; i < stonesWithYSpeed.size(); i++) {
                for (int j = i + 1; j < stonesWithYSpeed.size(); j++) {
                    Set<Integer> validSpeeds = new HashSet<>();
                    BigDecimal yDifference = stonesWithYSpeed.get(1).a.y.subtract(stonesWithYSpeed.get(0).a.y);
                    for (int velocity = -1000; velocity < 1000; velocity++) {
                        if (ySpeed != velocity && yDifference.remainder(new BigDecimal(ySpeed).subtract(new BigDecimal(velocity))).equals(BigDecimal.ZERO)) {
                            validSpeeds.add(velocity);
                        }
                    }
                    if (possibleYSpeeds.isEmpty()) {
                        possibleYSpeeds = validSpeeds;
                    } else {
                        possibleYSpeeds.retainAll(validSpeeds);
                    }
                }
            }
        }

        for (int zSpeed : commonZSpeeds) {
            List<Hailstone> stonesWithZSpeed = hailstones
                    .stream()
                    .filter(hs -> hs.velocity.z.intValue() == zSpeed)
                    .toList();
            for (int i = 0; i < stonesWithZSpeed.size(); i++) {
                for (int j = i + 1; j < stonesWithZSpeed.size(); j++) {
                    Set<Integer> validSpeeds = new HashSet<>();
                    BigDecimal zDifference = stonesWithZSpeed.get(1).a.z.subtract(stonesWithZSpeed.get(0).a.z);
                    for (int velocity = -1000; velocity < 1000; velocity++) {
                        if (zSpeed != velocity && zDifference.remainder(new BigDecimal(zSpeed).subtract(new BigDecimal(velocity))).equals(BigDecimal.ZERO)) {
                            validSpeeds.add(velocity);
                        }
                    }
                    if (possibleZSpeeds.isEmpty()) {
                        possibleZSpeeds = validSpeeds;
                    } else {
                        possibleZSpeeds.retainAll(validSpeeds);
                    }
                }
            }
        }

        Coordinates rockSpeed = new Coordinates(
                new BigDecimal(possibleXSpeeds.stream().findFirst().get()),
                new BigDecimal(possibleYSpeeds.stream().findFirst().get()),
                new BigDecimal(possibleZSpeeds.stream().findFirst().get())
        );

        Hailstone oldH1 = hailstones.get(0);
        Coordinates h1Speed = new Coordinates(oldH1.velocity.x.subtract(rockSpeed.x), oldH1.velocity.y.subtract(rockSpeed.y), oldH1.velocity.z.subtract(rockSpeed.z));
        Hailstone h1 = new Hailstone(oldH1.a, h1Speed);
        Hailstone oldH2 = hailstones.get(1);
        Coordinates h2Speed = new Coordinates(oldH2.velocity.x.subtract(rockSpeed.x), oldH2.velocity.y.subtract(rockSpeed.y), oldH2.velocity.z.subtract(rockSpeed.z));
        Hailstone h2 = new Hailstone(oldH2.a, h2Speed);


        Coordinates rockLocation = find2DIntersection(h1, h2);
        BigDecimal timeUntilRockHitsH1 = rockLocation.x.subtract(h1.a.x).divide(h1.velocity.x, 0, RoundingMode.DOWN);
        BigDecimal rockZLocation = h1.a.z.add(timeUntilRockHitsH1.multiply(h1.velocity.z));

        BigDecimal result = rockLocation.x.add(rockLocation.y).add(rockZLocation);

        return result.toBigInteger().toString();
    }

    private List<Hailstone> parseInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(this::parseInputLine)
                .toList();
    }

    private Hailstone parseInputLine(String line) {
        String[] components = line.split(" @ ");
        String[] positionComponents = components[0].split(",");
        Coordinates position = new Coordinates(
                new BigDecimal(positionComponents[0].trim()),
                new BigDecimal(positionComponents[1].trim()),
                new BigDecimal(positionComponents[2].trim())
        );
        String[] velocityComponents = components[1].split(",");
        Coordinates velocity = new Coordinates(
                new BigDecimal(velocityComponents[0].trim()),
                new BigDecimal(velocityComponents[1].trim()),
                new BigDecimal(velocityComponents[2].trim())
        );
        return new Hailstone(position, velocity);
    }

    private record Hailstone(Coordinates a, Coordinates velocity, Coordinates b) {
        private Hailstone(Coordinates a, Coordinates velocity) {
            this(a, velocity, new Coordinates(a.x.add(velocity.x), a.y.add(velocity.y), a.z.add(velocity.z)));
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        private boolean is2DPointInPast(Coordinates coordinates) {
            return (velocity.x.compareTo(BigDecimal.ZERO) < 0 && coordinates.x.compareTo(a.x) > 0)
                    || (velocity.x.compareTo(BigDecimal.ZERO) > 0 && coordinates.x.compareTo(a.x) < 0)
                    || (velocity.y.compareTo(BigDecimal.ZERO) < 0 && coordinates.y.compareTo(a.y) > 0)
                    || (velocity.y.compareTo(BigDecimal.ZERO) > 0 && coordinates.y.compareTo(a.y) < 0);
        }

        @Override
        public String toString() {
            return String.format("%s @ %s -> %s", a, velocity, b);
        }
    }

    private record Coordinates(BigDecimal x, BigDecimal y, BigDecimal z) {
        @Override
        public String toString() {
            return String.format("%s, %s, %s", x, y, z);
        }
    }

    private record TestArea(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
    }
}