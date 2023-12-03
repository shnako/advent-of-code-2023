package com.shnako.solutions.day03;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
We first extract all the symbols (as SchematicSymbol objects) and all the numbers (as SchematicNumber objects).
We store the coordinates for each symbol,
whereas for numbers we store the row coordinate as well as column coordinates for the start and end of the number.
We use these coordinates to determine adjacency.

Part 1:
For each symbol we find all the numbers adjacent to it. We then sum up all the numbers that are adjacent to a symbol.

Part 2:
For each * symbol we find all the numbers adjacent to it.
We then keep only the ones that have 2 adjacent numbers, multiply each symbol's numbers and sum them all up.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        final List<String> schematic = InputProcessingUtil.readInputLines(getDay());
        final List<SchematicSymbol> symbols = findSchematicSymbols(schematic);
        final List<SchematicNumber> numbers = findSchematicNumbers(schematic);
        int result = new HashSet<>(mapNumbersAdjacentToSymbols(symbols, numbers).values())
                .stream()
                .mapToInt(SchematicNumber::value)
                .sum();
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        final List<String> schematic = InputProcessingUtil.readInputLines(getDay());
        final List<SchematicSymbol> symbols = findSchematicSymbols(schematic).stream()
                .filter(s -> s.symbol == '*')
                .collect(Collectors.toList());
        final List<SchematicNumber> numbers = findSchematicNumbers(schematic);
        final Multimap<SchematicSymbol, SchematicNumber> gears = mapNumbersAdjacentToSymbols(symbols, numbers);
        int result = gears.keySet()
                .stream()
                .filter(symbol -> gears.get(symbol).size() == 2)
                .map(symbol -> new ArrayList<>(gears.get(symbol)))
                .mapToInt(values -> values.get(0).value * values.get(1).value)
                .sum();
        return String.valueOf(result);
    }

    private List<SchematicSymbol> findSchematicSymbols(List<String> schematic) {
        List<SchematicSymbol> symbolCoordinates = new ArrayList<>();
        for (int i = 0; i < schematic.size(); i++) {
            for (int j = 0; j < schematic.get(i).length(); j++) {
                char symbol = schematic.get(i).charAt(j);
                if (symbol != '.' && !Character.isDigit(symbol)) {
                    symbolCoordinates.add(new SchematicSymbol(symbol, i, j));
                }
            }
        }
        return symbolCoordinates;
    }

    private List<SchematicNumber> findSchematicNumbers(List<String> schematic) {
        List<SchematicNumber> result = new ArrayList<>();
        Pattern numberPattern = Pattern.compile("\\d+");
        for (int i = 0; i < schematic.size(); i++) {
            Matcher match = numberPattern.matcher(schematic.get(i));
            while (match.find()) {
                result.add(new SchematicNumber(Integer.parseInt(match.group()), i, match.start(), match.end() - 1));
            }
        }
        return result;
    }

    private Multimap<SchematicSymbol, SchematicNumber> mapNumbersAdjacentToSymbols(List<SchematicSymbol> symbols, List<SchematicNumber> numbers) {
        Multimap<SchematicSymbol, SchematicNumber> result = HashMultimap.create();
        for (SchematicSymbol symbol : symbols) {
            for (SchematicNumber number : numbers) {
                if (isNumberAdjacentToSymbol(number, symbol)) {
                    result.put(symbol, number);
                }
            }
        }
        return result;
    }

    private boolean isNumberAdjacentToSymbol(SchematicNumber number, SchematicSymbol symbol) {
        if (Math.abs(number.x - symbol.x) > 1) {
            return false;
        }
        return symbol.y >= number.yStart - 1 && symbol.y <= number.yEnd + 1;
    }

    private record SchematicSymbol(char symbol, int x, int y) {
    }

    private record SchematicNumber(int value, int x, int yStart, int yEnd) {
    }
}