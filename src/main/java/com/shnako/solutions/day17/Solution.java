package com.shnako.solutions.day17;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/*
We use an adapted version of Dijkstra's algorithm to find the distances to every point in the city from the start.
When determining if a node has been visited, we consider its coordinates,
as well as the incoming direction and the current straight length.
So for example, if we visited node [X, Y] from the left on a straight length of 2,
then visiting the same node from the top on any straight length is considered a new visit.
This allows us to find the optimal distance for each node from any direction and straight length.

Part 1:
We only check for a maximum straight length.
The result is the minimum distance on any visit of the bottom right node.

Part 2:
We also check the minimum straight length before allowing any turns.
The result is the minimum distance on any visit of the bottom right node.
 */

public class Solution extends SolutionBase {
    private int[][] city;
    private int minStraightLength;
    private int maxStraightLength;
    private static Map<NodeVisit, Integer> minVisitedNodes;
    private PriorityQueue<NodeVisit> nextNodes;

    @Override
    public String runPart1() throws IOException {
        minStraightLength = 0;
        maxStraightLength = 3;
        return solve();
    }

    @Override
    public String runPart2() throws IOException {
        minStraightLength = 4;
        maxStraightLength = 10;
        return solve();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private String solve() throws IOException {
        city = parseInput();
        findMinDistances();
        int result = minVisitedNodes.keySet()
                .stream()
                .filter(nodeValue -> nodeValue.r == city.length - 1 && nodeValue.c == city[0].length - 1)
                .mapToInt(nv -> minVisitedNodes.get(nv))
                .min()
                .getAsInt();
        return String.valueOf(result);
    }

    private void findMinDistances() {
        nextNodes = new PriorityQueue<>();
        minVisitedNodes = new HashMap<>();
        NodeVisit startingNode = new NodeVisit(0, 0, new NodeVisit(0, 0, null, 0), 0);
        addNextNode(startingNode);

        while (nextNodes.peek() != null) {
            NodeVisit thisNodeVisit = nextNodes.poll();
            for (int rNext = -1; rNext <= 1; rNext++) {
                for (int cNext = -1; cNext <= 1; cNext++) {
                    if (Math.abs(rNext) + Math.abs(cNext) == 1) {
                        if (thisNodeVisit.r + rNext < 0 || thisNodeVisit.r + rNext >= city.length
                                || thisNodeVisit.c + cNext < 0 || thisNodeVisit.c + cNext >= city[0].length) {
                            // No going out of the city.
                            continue;
                        }
                        if (thisNodeVisit.r + rNext == thisNodeVisit.fromNode.r && thisNodeVisit.c + cNext == thisNodeVisit.fromNode.c) {
                            // No going back.
                            continue;
                        }
                        if (thisNodeVisit.r - thisNodeVisit.fromNode.r == rNext && thisNodeVisit.c - thisNodeVisit.fromNode.c == cNext) {
                            if (maxStraightLength == thisNodeVisit.straightLength) {
                                // No going straight more than the maximum allowed.
                                continue;
                            }
                            // Going straight.
                            NodeVisit nextNodeVisit = new NodeVisit(thisNodeVisit.r + rNext, thisNodeVisit.c + cNext, thisNodeVisit, thisNodeVisit.straightLength + 1);
                            addNextNode(nextNodeVisit);
                            continue;
                        }
                        if (isStartingNode(thisNodeVisit) || minStraightLength <= thisNodeVisit.straightLength) {
                            // Going perpendicular only on starting node or if minimum straight length achieved.
                            NodeVisit nextNodeVisit = new NodeVisit(thisNodeVisit.r + rNext, thisNodeVisit.c + cNext, thisNodeVisit, 1);
                            addNextNode(nextNodeVisit);
                        }
                    }
                }
            }
        }
    }

    private void addNextNode(NodeVisit nodeVisit) {
        int distance = isStartingNode(nodeVisit) ? 0 : minVisitedNodes.get(nodeVisit.fromNode) + city[nodeVisit.r][nodeVisit.c];
        if (minVisitedNodes.containsKey(nodeVisit)) {
            if (minVisitedNodes.get(nodeVisit) > distance) {
                minVisitedNodes.put(nodeVisit, distance);
                nextNodes.add(nodeVisit);
            }
        } else {
            minVisitedNodes.put(nodeVisit, distance);
            nextNodes.add(nodeVisit);
        }
    }

    private boolean isStartingNode(NodeVisit nodeVisit) {
        return nodeVisit.fromNode.fromNode == null;
    }

    private int[][] parseInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(line -> line.chars().map(c -> c - '0').toArray())
                .toArray(int[][]::new);
    }

    private record NodeVisit(int r, int c, NodeVisit fromNode, int straightLength) implements Comparable<NodeVisit> {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NodeVisit nodeVisit = (NodeVisit) o;

            if (r != nodeVisit.r) return false;
            if (c != nodeVisit.c) return false;
            if (fromNode != null && ((NodeVisit) o).fromNode != null) {
                if (fromNode.r != ((NodeVisit) o).fromNode.r) return false;
                if (fromNode.c != ((NodeVisit) o).fromNode.c) return false;
            }
            return straightLength == nodeVisit.straightLength;
        }

        @Override
        public int hashCode() {
            int prime = 5779; // The default 31 is not enough so needed to increase it to something better.
            int result = r;
            result = prime * result + c;
            result = prime * result + (fromNode != null ? fromNode.r : 0);
            result = prime * result + (fromNode != null ? fromNode.c : 0);
            result = prime * result + straightLength;
            return result;
        }

        @Override
        public int compareTo(@NotNull NodeVisit other) {
            return minVisitedNodes.get(this) - minVisitedNodes.get(other);
        }
    }
}
