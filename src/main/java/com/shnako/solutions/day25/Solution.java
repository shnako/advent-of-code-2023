package com.shnako.solutions.day25;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
After wasting so much family time solving day 24, I decided to only try day 25 on the day if it's trivial, and it's not.
Therefore, I focused on having a good Christmas day and implemented this in February 2024.

Part 1:
The solution is based on Karger's algorithm: https://en.wikipedia.org/wiki/Karger%27s_algorithm
We read the input into a list of Node objects, each containing Edge objects knowing what Nodes they are between.
Because Karger's algorithm is randomised, we repeat it until we get the result we expect - 3 edges to be cut.
Theoretically, it can finish in 1 attempt, or it could never finish, but it usually finishes in <50 attempts.

For each attempt, we pick a random edge and contract it as per the algorithm.
We keep a list of nodes that have been contracted into each node as the algorithm progresses.
We keep contracting until we only have 2 nodes remaining.
If the nodes have exactly 3 edges between them, we know we've got the result.
The result is the number of nodes contracted into the 1st multiplied by the number of nodes contracted into the 2nd.

Part 2:
We simply need to have solved all the previous problems to get the star so nothing to code.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        int result = -1, attempts = 0;
        Random randomiser = new Random();
        while (result == -1) {
            List<Node> nodes = parseInput();

            while (nodes.size() > 2) {
                int nodeIndex = randomiser.nextInt(nodes.size());
                int edgeIndex = randomiser.nextInt(nodes.get(nodeIndex).getEdges().size());
                Edge randomEdge = nodes.get(nodeIndex).getEdges().get(edgeIndex);
                contractEdge(nodes, randomEdge);
            }

            if (nodes.getFirst().getEdges().size() == 3) {
                result = nodes.getFirst().getContractedNodes().size() * nodes.getLast().getContractedNodes().size();
            } else {
                System.out.println("Attempt " + ++attempts + " found " + nodes.getFirst().getEdges().size() + " cuts.");
            }
        }

        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        return "Merry Christmas!";
    }

    private void contractEdge(List<Node> nodes, Edge edge) {
        Node n1 = edge.getNode1();
        Node n2 = edge.getNode2();

        for (Edge n2Edge : n2.getEdges()) {
            if (n2Edge.getNode1() == n1 || n2Edge.getNode2() == n1) {
                n1.getEdges().remove(n2Edge);
                continue;
            }

            if (n2Edge.getNode1() == n2) {
                n2Edge.moveEdge(n1, n2Edge.getNode2());
            } else {
                n2Edge.moveEdge(n2Edge.getNode1(), n1);
            }
            n1.addEdge(n2Edge);
        }

        n1.addContractedNodes(n2.getContractedNodes());
        nodes.remove(n2);
    }

    private List<Node> parseInput() throws IOException {
        Map<String, Node> wiringDiagram = new HashMap<>();
        for (String line : InputProcessingUtil.readInputLines(getDay())) {
            String[] components = line.split(": ");
            String c1 = components[0];

            if (!wiringDiagram.containsKey(c1)) {
                wiringDiagram.put(c1, new Node(c1));
            }

            components = components[1].split(" ");
            for (String c2 : components) {
                if (!wiringDiagram.containsKey(c2)) {
                    wiringDiagram.put(c2, new Node(c2));
                }
                Edge edge = new Edge(wiringDiagram.get(c1), wiringDiagram.get(c2));
                wiringDiagram.get(c1).addEdge(edge);
                wiringDiagram.get(c2).addEdge(edge);
            }
        }
        return new ArrayList<>(wiringDiagram.values());
    }

    private class Node {
        private final String id;
        private final List<Edge> edges;
        private final List<String> contractedNodes;

        private Node(String id) {
            edges = new ArrayList<>();
            contractedNodes = new ArrayList<>();
            this.id = id;
            contractedNodes.add(id);
        }

        private String getId() {
            return id;
        }

        private void addEdge(Edge edge) {
            edges.add(edge);
        }

        private List<Edge> getEdges() {
            return edges;
        }

        private void addContractedNodes(List<String> newContractedNodes) {
            contractedNodes.addAll(newContractedNodes);
        }

        private List<String> getContractedNodes() {
            return contractedNodes;
        }

        @Override
        public String toString() {
            return id + ": " + edges.stream()
                    .map(x -> x.getNode1() == this ? x.getNode2().getId() : x.getNode1().getId())
                    .collect(Collectors.joining(", "));
        }
    }

    private class Edge {
        private Node node1, node2;

        private Edge(Node node1, Node node2) {
            this.node1 = node1;
            this.node2 = node2;
        }

        private void moveEdge(Node node1, Node node2) {
            this.node1 = node1;
            this.node2 = node2;
        }

        private Node getNode1() {
            return node1;
        }

        private Node getNode2() {
            return node2;
        }

        @Override
        public String toString() {
            return node1.getId() + " -> " + node2.getId();
        }
    }
}