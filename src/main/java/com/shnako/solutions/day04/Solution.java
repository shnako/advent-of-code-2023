package com.shnako.solutions.day04;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
We parse the input into Card records containing the id, set of winning numbers and set of card numbers.
We then do a set intersection between the winning numbers and the card numbers to find the matches,
and store them in the card's winning numbers.

Part 1:
For each card we calculate the points as being 2 ^ (matchCount - 1). The result is the sum of these points.

Part 2:
We go through the list of cards and build a map of how many copies of each card we have according to the rules.
The result is the total number of card copies.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Card> cards = parseInput();
        cards.forEach(card -> card.winningNumbers.retainAll(card.cardNumbers));

        long result = cards.stream()
                .map(card -> card.winningNumbers.size())
                .filter(matchCount -> matchCount > 0)
                .mapToLong(matchCount -> Math.round(Math.pow(2, matchCount - 1)))
                .sum();
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        List<Card> cards = parseInput();
        cards.forEach(card -> card.winningNumbers.retainAll(card.cardNumbers));

        Map<Integer, Integer> cardCopies = new HashMap<>();
        for (Card card : cards) {
            cardCopies.put(card.id, cardCopies.containsKey(card.id) ? cardCopies.get(card.id) + 1 : 1);
            int currentCardCopies = cardCopies.get(card.id);
            int matchCount = card.winningNumbers.size();
            for (int i = 1; i <= matchCount; i++) {
                cardCopies.put(card.id + i, cardCopies.containsKey(card.id + i)
                        ? cardCopies.get(card.id + i) + currentCardCopies : currentCardCopies);
            }
        }

        int result = cardCopies.values()
                .stream()
                .mapToInt(x -> x)
                .sum();
        return String.valueOf(result);
    }

    private List<Card> parseInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(this::parseInputLine)
                .toList();
    }

    private Card parseInputLine(String line) {
        String[] colonSplit = line.split(":");
        int cardId = Integer.parseInt(colonSplit[0].substring(4).trim());
        String[] pipeSplit = colonSplit[1].split("\\|");
        Set<Integer> winningNumbers = Arrays.stream(pipeSplit[0].trim().split(" "))
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        Set<Integer> cardNumbers = Arrays.stream(pipeSplit[1].trim().split(" "))
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        return new Card(cardId, winningNumbers, cardNumbers);
    }

    private record Card(int id, Set<Integer> winningNumbers, Set<Integer> cardNumbers) {
    }
}