package com.shnako.solutions.day05;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/*
We read everything into an Almanac containing a list of seeds and a list of maps.
Every AlmanacMap contains a list of ranges sorted by the source start number.

Part 1:
We determine the location for each seed by mapping from source to destination through the maps.
The result is the minimum of all seed locations.

Part 2:
This was much harder than I would expect for day 5. The number of seeds is far too big to run like part 1.
The strategy here is to end up with a map of seed to location ranges.
For each range in the seeds we recursively go through all the maps, slicing ranges as required.
Once we are on the last map, we add the range mappings to seedToLocationRanges.
The result is the minimum starting destination of all these ranges.
 */
public class Solution extends SolutionBase {
    private final List<Range> seedToLocationRanges = new ArrayList<>();

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String runPart1() throws IOException {
        Almanac almanac = parseInput();
        long result = almanac.seeds
                .stream()
                .mapToLong(almanac::findSeedLocation)
                .min()
                .getAsLong();
        return String.valueOf(result);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public String runPart2() throws IOException {
        Almanac almanac = parseInput();
        for (int i = 0; i < almanac.seeds.size(); i += 2) {
            buildRanges(almanac, 0, almanac.seeds.get(i), almanac.seeds.get(i + 1), almanac.seeds.get(i));
        }
        long result = seedToLocationRanges
                .stream()
                .mapToLong(x -> x.destinationStart)
                .min()
                .getAsLong();
        return String.valueOf(result);
    }

    private void buildRanges(Almanac almanac, int mapIndex, long sourceStart, long length, long seedSourceStart) {
        List<Range> rangesOnMap = almanac.almanacMaps.get(mapIndex).findDestinationRangesMatchingSourceRange(sourceStart, length);

        if (mapIndex == almanac.almanacMaps.size() - 1) {
            for (Range range : rangesOnMap) {
                seedToLocationRanges.add(new Range(range.destinationStart, seedSourceStart, length));
                seedSourceStart += range.length;
            }
        } else {
            for (Range range : rangesOnMap) {
                buildRanges(almanac, mapIndex + 1, range.destinationStart, range.length, seedSourceStart);
                seedSourceStart += range.length;
            }
        }
    }

    private Almanac parseInput() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());

        List<Long> seeds = Arrays.stream(input.get(0).substring(7).split(" "))
                .map(Long::parseLong)
                .toList();

        List<AlmanacMap> almanacMaps = new ArrayList<>(7);
        AlmanacMap almanacMap = null;
        for (String line : input.subList(1, input.size())) {
            if (StringUtils.isBlank(line)) {
                almanacMap = new AlmanacMap();
                almanacMaps.add(almanacMap);
                continue;
            }
            if (Character.isAlphabetic(line.charAt(0))) {
                continue;
            }
            List<Long> rangeNumbers = Arrays.stream(line.split(" "))
                    .map(Long::parseLong)
                    .toList();

            Range range = new Range(rangeNumbers.get(0), rangeNumbers.get(1), rangeNumbers.get(2));
            Objects.requireNonNull(almanacMap).addRange(range);
        }
        return new Almanac(seeds, almanacMaps);
    }

    private record Almanac(List<Long> seeds, List<AlmanacMap> almanacMaps) {
        private long findSeedLocation(long seed) {
            long currentNumber = seed;
            for (AlmanacMap almanacMap : almanacMaps) {
                currentNumber = almanacMap.getDestination(currentNumber);
            }
            return currentNumber;
        }
    }

    private static class AlmanacMap {
        private final List<Range> ranges = new ArrayList<>();

        private void addRange(Range range) {
            ranges.add(range);
            ranges.sort(Comparator.comparing(a -> a.sourceStart));
        }

        private long getDestination(long source) {
            for (Range range : ranges) {
                if (source >= range.sourceStart && source <= range.sourceStart + range.length) {
                    long offset = source - range.sourceStart;
                    return range.destinationStart + offset;
                }
            }
            return source;
        }

        private List<Range> findDestinationRangesMatchingSourceRange(long rangeStart, long rangeLength) {
            List<Range> result = new ArrayList<>();
            for (int i = 0; i < ranges.size() && rangeLength > 0; i++) {
                Range thisRange = ranges.get(i);
                if (rangeStart > thisRange.sourceStart + thisRange.length) {
                    continue;
                }

                if (rangeStart < thisRange.sourceStart) {
                    if (rangeStart + rangeLength < thisRange.sourceStart) {
                        result.add(new Range(rangeStart, rangeStart, rangeLength));
                        rangeLength = 0;
                    } else if (rangeStart + rangeLength < thisRange.sourceStart + thisRange.length) {
                        result.add(new Range(rangeStart, rangeStart, thisRange.sourceStart - rangeStart));
                        result.add(new Range(thisRange.destinationStart, thisRange.sourceStart, rangeStart + rangeLength - thisRange.sourceStart));
                        rangeLength = 0;
                    } else {
                        result.add(new Range(rangeStart, rangeStart, thisRange.sourceStart - rangeStart));
                        result.add(new Range(thisRange.destinationStart, thisRange.sourceStart, thisRange.length));

                        long newRangesLength = thisRange.sourceStart + thisRange.length - rangeStart;
                        rangeLength -= newRangesLength;
                        rangeStart += newRangesLength;
                    }
                } else {
                    long offset = rangeStart - thisRange.sourceStart;
                    if (rangeStart + rangeLength < thisRange.sourceStart + thisRange.length) {
                        result.add(new Range(thisRange.destinationStart + offset, thisRange.sourceStart + offset, rangeLength));
                        rangeLength = 0;
                    } else {
                        result.add(new Range(thisRange.destinationStart + offset, thisRange.sourceStart + offset, thisRange.length - offset));

                        long newRangeLength = thisRange.length - offset;
                        rangeLength -= newRangeLength;
                        rangeStart += newRangeLength;
                    }
                }
            }
            if (rangeLength > 0) {
                result.add(new Range(rangeStart, rangeStart, rangeLength));
            }
            return result;
        }
    }

    private record Range(long destinationStart, long sourceStart, long length) {
    }
}