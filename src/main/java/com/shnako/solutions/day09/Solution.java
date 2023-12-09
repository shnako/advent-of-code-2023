package com.shnako.solutions.day09;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
Very straightforward problem today. The solution is to just implement what the problem asks for, nothing fancy.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<List<Integer>> histories = parseInput();
        int result = histories
                .stream()
                .map(this::generateSequences)
                .mapToInt(this::getValueExtrapolatedAtTheEnd)
                .sum();
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        List<List<Integer>> histories = parseInput();
        int result = histories
                .stream()
                .map(this::generateSequences)
                .mapToInt(this::getValueExtrapolatedAtTheBeginning)
                .sum();
        return String.valueOf(result);
    }

    private LinkedList<LinkedList<Integer>> generateSequences(List<Integer> history) {
        LinkedList<LinkedList<Integer>> sequences = new LinkedList<>();
        sequences.add(new LinkedList<>(history));
        while (!isFinalSequence(sequences.getLast())) {
            LinkedList<Integer> sequence = new LinkedList<>();
            for (int i = 0; i < sequences.getLast().size() - 1; i++) {
                sequence.add(sequences.getLast().get(i + 1) - sequences.getLast().get(i));
            }
            sequences.add(sequence);
        }
        return sequences;
    }

    private boolean isFinalSequence(LinkedList<Integer> sequence) {
        return sequence.stream().allMatch(value -> value == 0);
    }

    private int getValueExtrapolatedAtTheEnd(LinkedList<LinkedList<Integer>> sequences) {
        for (int i = sequences.size() - 2; i >= 0; i--) {
            sequences.get(i).add(sequences.get(i).getLast() + sequences.get(i + 1).getLast());
        }
        return sequences.get(0).getLast();
    }

    private int getValueExtrapolatedAtTheBeginning(LinkedList<LinkedList<Integer>> sequences) {
        for (int i = sequences.size() - 2; i >= 0; i--) {
            sequences.get(i).addFirst(sequences.get(i).getFirst() - sequences.get(i + 1).getFirst());
        }
        return sequences.get(0).getFirst();
    }

    private List<List<Integer>> parseInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(line -> Arrays.stream(line.split(" "))
                        .map(Integer::parseInt)
                        .toList())
                .toList();
    }
}