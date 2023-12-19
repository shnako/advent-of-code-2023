package com.shnako.solutions.day19;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

/*
Part 1:
For each part, we recursively verify conditions until we determine whether it's accepted or not.
The result is the sum of all part ratings.

Part 2:
The dataset is far too large so a different approach is needed.
We start with a PartInterval containing the full dataset (1 to 4000 for each component).
We then recursively break down the interval based on the conditions, until we determine whether it's accepted or not.
We end up with a list of intervals where every combination in that interval is either accepted or not.
The result is the number of elements in the accepted intervals.

I spent a very long time debugging the application for part 2
because I was efficiently calculating the sum of all part ratings in the accepted intervals,
rather than the number of accepted parts.
Figuring out the formula for this took a long time as well so part 2 took me much longer than it should have.
 */
public class Solution extends SolutionBase {
    // region Part 1

    @Override
    public String runPart1() throws IOException {
        Pair<Map<String, List<Condition>>, List<Part>> input = parseInput();
        Map<String, List<Condition>> workflows = input.getLeft();
        List<Part> parts = input.getRight();
        int result = parts.stream()
                .filter(part -> isPartAccepted(part, workflows, workflows.get("in")))
                .mapToInt(Part::getRating)
                .sum();
        return String.valueOf(result);
    }

    private boolean isPartAccepted(Part part, Map<String, List<Condition>> workflows, List<Condition> currentWorkflow) {
        for (Condition condition : currentWorkflow) {
            if ((condition.value == 0)
                    || condition.operator == '<' && part.getValue(condition.operand) < condition.value
                    || condition.operator == '>' && part.getValue(condition.operand) > condition.value) {
                return switch (condition.result) {
                    case "A" -> true;
                    case "R" -> false;
                    default -> isPartAccepted(part, workflows, workflows.get(condition.result));
                };
            }
        }
        throw new RuntimeException("Couldn't verify part.");
    }

    // endregion

    // region Part 2

    @Override
    public String runPart2() throws IOException {
        Pair<Map<String, List<Condition>>, List<Part>> input = parseInput();
        Map<String, List<Condition>> workflows = input.getLeft();
        PartInterval fullInterval = new PartInterval(1, 4000, 1, 4000, 1, 4000, 1, 4000, null, null);
        List<PartInterval> splitIntervals = splitByWorkflows(fullInterval, workflows, workflows.get("in"));
        long result = splitIntervals
                .stream()
                .filter(PartInterval::accepted)
                .mapToLong(PartInterval::getIntervalSize)
                .sum();
        return String.valueOf(result);
    }

    private List<PartInterval> splitByWorkflows(PartInterval interval, Map<String, List<Condition>> workflows, List<Condition> currentWorkflow) {
        List<PartInterval> result = new ArrayList<>();
        PartInterval remainingInterval = interval;
        for (Condition condition : currentWorkflow) {
            if (condition.value == 0) {
                switch (condition.result) {
                    case "A" -> result.add(remainingInterval.getWithAccepted(true));
                    case "R" -> result.add(remainingInterval.getWithAccepted(false));
                    default ->
                            result.addAll(splitByWorkflows(remainingInterval, workflows, workflows.get(condition.result)));
                }
            } else {
                List<PartInterval> splitIntervals = remainingInterval.split(condition);
                for (PartInterval splitInterval : splitIntervals) {
                    if (splitInterval.matched) {
                        switch (condition.result) {
                            case "A" -> result.add(splitInterval.getWithAccepted(true));
                            case "R" -> result.add(splitInterval.getWithAccepted(false));
                            default ->
                                    result.addAll(splitByWorkflows(splitInterval, workflows, workflows.get(condition.result)));
                        }
                    } else {
                        remainingInterval = splitInterval;
                    }
                }
            }
        }
        return result;
    }

    private record RangeSegment(int from, int to, boolean matched) {
    }

    private record PartInterval(int x1, int x2, int m1, int m2, int a1, int a2, int s1, int s2, Boolean accepted,
                                Boolean matched) {
        private PartInterval getWithAccepted(Boolean newAccepted) {
            return new PartInterval(x1, x2, m1, m2, a1, a2, s1, s2, newAccepted, matched);
        }

        private List<PartInterval> split(Condition condition) {
            List<RangeSegment> splits = switch (condition.operand) {
                case 'x' -> getRangeSegments(x1, x2, condition.value, condition.operator);
                case 'm' -> getRangeSegments(m1, m2, condition.value, condition.operator);
                case 'a' -> getRangeSegments(a1, a2, condition.value, condition.operator);
                case 's' -> getRangeSegments(s1, s2, condition.value, condition.operator);
                default -> throw new RuntimeException("Invalid range id.");
            };

            return splits
                    .stream()
                    .map(segment -> switch (condition.operand) {
                        case 'x' ->
                                new PartInterval(segment.from, segment.to, m1, m2, a1, a2, s1, s2, accepted, segment.matched);
                        case 'm' ->
                                new PartInterval(x1, x2, segment.from, segment.to, a1, a2, s1, s2, accepted, segment.matched);
                        case 'a' ->
                                new PartInterval(x1, x2, m1, m2, segment.from, segment.to, s1, s2, accepted, segment.matched);
                        case 's' ->
                                new PartInterval(x1, x2, m1, m2, a1, a2, segment.from, segment.to, accepted, segment.matched);
                        default -> throw new RuntimeException("Invalid range id.");
                    })
                    .toList();
        }

        private List<RangeSegment> getRangeSegments(int from, int to, int splitValue, char operator) {
            if (splitValue < from) {
                return List.of(new RangeSegment(from, to, operator == '<'));
            }
            if (splitValue > to) {
                return List.of(new RangeSegment(from, to, operator == '>'));
            }
            return List.of(
                    new RangeSegment(from, splitValue - (operator == '<' ? 1 : 0), operator == '<'),
                    new RangeSegment(splitValue + (operator == '>' ? 1 : 0), to, operator == '>'));
        }

        private long getIntervalSize() {
            return (long) ((x2 - x1 + 1)) * (m2 - m1 + 1) * (a2 - a1 + 1) * (s2 - s1 + 1);
        }
    }

    // endregion

    // region Input parsing

    private Pair<Map<String, List<Condition>>, List<Part>> parseInput() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        int i;
        Map<String, List<Condition>> workflow = new HashMap<>();
        for (i = 0; StringUtils.isNotBlank(input.get(i)); i++) {
            String[] components = input.get(i).split("\\{");
            String workflowId = components[0];
            String[] conditionStrings = components[1].substring(0, components[1].length() - 1).split(",");
            List<Condition> conditions = Arrays.stream(conditionStrings).map(this::parseCondition).toList();
            workflow.put(workflowId, conditions);
        }

        List<Part> parts = new ArrayList<>();
        for (i = i + 1; i < input.size(); i++) {
            String[] components = input.get(i).substring(1, input.get(i).length() - 1).split(",");
            int x = Integer.parseInt(components[0].substring(2));
            int m = Integer.parseInt(components[1].substring(2));
            int a = Integer.parseInt(components[2].substring(2));
            int s = Integer.parseInt(components[3].substring(2));
            parts.add(new Part(x, m, a, s));
        }

        return Pair.of(workflow, parts);
    }

    private Condition parseCondition(String conditionString) {
        String[] components = conditionString.split(":");
        if (components.length == 1) {
            return new Condition((char) 0, (char) 0, 0, conditionString);
        } else {
            String result = components[1];
            String[] conditionComponents = components[0].split("((?<=[<>])|(?=[<>]))");
            char operand = conditionComponents[0].charAt(0);
            char operator = conditionComponents[1].charAt(0);
            int value = Integer.parseInt(conditionComponents[2]);
            return new Condition(operand, operator, value, result);
        }
    }

    private record Condition(char operand, char operator, int value, String result) {
    }

    private record Part(int x, int m, int a, int s) {
        private int getValue(char valueId) {
            return switch (valueId) {
                case 'x' -> x;
                case 'm' -> m;
                case 'a' -> a;
                case 's' -> s;
                default -> throw new RuntimeException("Unexpected value id.");
            };
        }

        private int getRating() {
            return x + m + a + s;
        }
    }

    // endregion
}