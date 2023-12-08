package com.shnako.solutions.day08;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
Part 1:
We navigate the network as detailed.
The result is the number of steps required to reach the destination.

Part 2:
We can't simply navigate this as the number of steps is far too large.
Thankfully it looks like for each navigation,
we cyclically end up at the same destination after the same number of steps it took us to get there the first time.
We therefore only need to determine how many steps are needed to reach the destination for each navigation.
The result is then the least common multiple of these numbers.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        Network network = parseInput();
        int result = navigate("AAA", "ZZZ", network);
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        Network network = parseInput();

        BigInteger result = network.nodes.keySet().stream()
                .filter(from -> from.endsWith("A"))
                .map(from -> navigate(from, "Z", network))
                .map(BigInteger::valueOf)
                .reduce(BigInteger.valueOf(1), Solution::lcm);

        return result.toString();
    }

    private int navigate(String from, String to, Network network) {
        int directionIndex = 0;
        String currentNode = from;
        int steps = 0;
        while (!currentNode.endsWith(to)) {
            if (network.directions.get(directionIndex)) {
                currentNode = network.nodes.get(currentNode).getRight();
            } else {
                currentNode = network.nodes.get(currentNode).getLeft();
            }
            steps++;
            directionIndex = (directionIndex + 1) % network.directions.size();
        }
        return steps;
    }

    public static BigInteger lcm(BigInteger number1, BigInteger number2) {
        BigInteger gcd = number1.gcd(number2);
        BigInteger absProduct = number1.multiply(number2).abs();
        return absProduct.divide(gcd);
    }

    private Network parseInput() throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());

        List<Boolean> directions = input.get(0).chars().mapToObj(c -> c == 'R').toList();

        Map<String, Pair<String, String>> nodes = new HashMap<>();
        for (int i = 2; i < input.size(); i++) {
            String[] components = input.get(i).split(" = ");
            String from = components[0];
            String[] tos = components[1].replace("(", "").replace(")", "").split(", ");
            nodes.put(from, Pair.of(tos[0], tos[1]));
        }

        return new Network(directions, nodes);
    }

    private record Network(List<Boolean> directions, Map<String, Pair<String, String>> nodes) {
    }
}