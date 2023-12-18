package com.shnako.solutions.day18;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
We first parse the input into a list of lines.
We then use the Shoelace Formula to calculate the internal area:
https://en.wikipedia.org/wiki/Shoelace_formula#Shoelace_formula
Once we have the area and the perimeter, we can calculate the total area using Pick's theorem:
https://en.wikipedia.org/wiki/Pick%27s_theorem

Part 1:
We generate the lines based on the first part of the input.
The result is the total area calculated as detailed above.

Part 2:
We generate the lines based on the hex colour codes of the input.
The result is the total area calculated as detailed above.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Line> lines = parseInputForP1();
        long result = calculateArea(lines);
        return String.valueOf(result);
    }

    private List<Line> parseInputForP1() throws IOException {
        List<String> digPlan = InputProcessingUtil.readInputLines(getDay());
        List<Line> result = new ArrayList<>(digPlan.size());
        int currentX = 0, currentY = 0;
        for (String inputLine : digPlan) {
            String[] components = inputLine.split(" ");
            char direction = components[0].charAt(0);
            int length = Integer.parseInt(components[1]);
            int nextX = currentX, nextY = currentY;
            switch (direction) {
                case 'U' -> nextX -= length;
                case 'D' -> nextX += length;
                case 'L' -> nextY -= length;
                case 'R' -> nextY += length;
            }
            Line line = new Line(currentX, currentY, nextX, nextY);
            result.add(line);
            currentX = nextX;
            currentY = nextY;
        }
        return result;
    }

    @Override
    public String runPart2() throws IOException {
        List<Line> lines = parseInputForP2();
        long result = calculateArea(lines);
        return String.valueOf(result);
    }

    private List<Line> parseInputForP2() throws IOException {
        List<String> digPlan = InputProcessingUtil.readInputLines(getDay());
        List<Line> result = new ArrayList<>(digPlan.size());
        int currentX = 0, currentY = 0;
        for (String inputLine : digPlan) {
            String[] components = inputLine.split(" ");
            String hexColour = components[2].substring(1, components[2].length() - 1);
            int length = Integer.parseInt(hexColour.substring(1, hexColour.length() - 1), 16);
            int nextX = currentX, nextY = currentY;

            switch (hexColour.charAt(hexColour.length() - 1)) {
                case '3' -> nextX -= length;
                case '1' -> nextX += length;
                case '2' -> nextY -= length;
                case '0' -> nextY += length;
            }
            Line line = new Line(currentX, currentY, nextX, nextY);
            result.add(line);
            currentX = nextX;
            currentY = nextY;
        }
        return result;
    }

    private long calculateArea(List<Line> lines) {
        long interiorArea = 0, perimeter = 0;
        for (Line line : lines) {
            interiorArea += (long) line.x1 * line.y2 - (long) line.y1 * line.x2;
            perimeter += Math.abs(line.x2 - line.x1) + Math.abs(line.y2 - line.y1);
        }
        return (Math.abs(interiorArea) + perimeter) / 2 + 1;
    }

    private record Line(int x1, int y1, int x2, int y2) {
    }
}