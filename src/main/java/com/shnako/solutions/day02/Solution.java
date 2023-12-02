package com.shnako.solutions.day02;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/*
Today's problem is fairly straightforward once the input is parsed.

Part 1:
Filter out all the games that have more cubes of any colour than specified and sum the IDs of the remaining ones.

Part 2:
Determine the maximum number of cubes of each colour, in each set, in each game and calculate the value using those.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        final Map<String, Integer> actualCubes = Map.of("red", 12, "green", 13, "blue", 14);

        List<Game> games = parseInput();

        return String.valueOf(getPossibleGamesSum(games, actualCubes));
    }

    private int getPossibleGamesSum(List<Game> games, Map<String, Integer> actualCubes) {
        return games.stream()
                .filter(game -> isGamePossible(game, actualCubes))
                .map(Game::gameId)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private boolean isGamePossible(Game game, Map<String, Integer> actualCubes) {
        for (CubeSet cubeSet : game.cubeSets()) {
            for (Map.Entry<String, Integer> cubeEntry : cubeSet.cubes().entrySet()) {
                if (!actualCubes.containsKey(cubeEntry.getKey()) || cubeEntry.getValue() > actualCubes.get(cubeEntry.getKey())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String runPart2() throws IOException {
        List<Game> games = parseInput();

        return String.valueOf(getSumOfPowerOfMinimumSetsOfCubes(games));
    }

    private int getSumOfPowerOfMinimumSetsOfCubes(List<Game> games) {
        return games.stream()
                .map(this::getGamePower)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private int getGamePower(Game game) {
        Map<String, Integer> maxColours = new HashMap<>();
        for (CubeSet cubeSet : game.cubeSets()) {
            for (Map.Entry<String, Integer> cubeEntry : cubeSet.cubes().entrySet()) {
                if (!maxColours.containsKey(cubeEntry.getKey()) || maxColours.get(cubeEntry.getKey()) < cubeEntry.getValue()) {
                    maxColours.put(cubeEntry.getKey(), cubeEntry.getValue());
                }
            }
        }
        return maxColours.values()
                .stream()
                .reduce(1, (subtotal, element) -> subtotal * element);
    }

    private List<Game> parseInput() throws IOException {
        List<String> inputLines = InputProcessingUtil.readInputLines(getDay());
        return inputLines.stream()
                .map(this::parseInputLine)
                .collect(Collectors.toList());
    }

    private Game parseInputLine(String line) {
        String[] colonSplit = line.split(":");
        int gameId = Integer.parseInt(colonSplit[0].substring(5));
        String[] setStrings = colonSplit[1].trim().split(";");

        List<CubeSet> cubeSets = new ArrayList<>();
        for (String setString : setStrings) {
            String[] cubeStrings = setString.trim().split(",");

            CubeSet cubeSet = new CubeSet(Arrays.stream(cubeStrings)
                    .map(x -> x.trim().split(" "))
                    .collect(Collectors.toMap(x -> x[1], x -> Integer.parseInt(x[0]))));
            cubeSets.add(cubeSet);
        }

        return new Game(gameId, cubeSets);
    }

    private record Game(Integer gameId, List<CubeSet> cubeSets) {
    }

    private record CubeSet(Map<String, Integer> cubes) {
    }
}