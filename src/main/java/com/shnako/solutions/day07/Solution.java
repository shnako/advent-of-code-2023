package com.shnako.solutions.day07;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
Part 1:
While reading the cards, we replace the letter cards with numbers to reflect their ordering.
We determine for each hand what type it is and store it as an enum with integer value.
We then sort the hands by the enum numbers and from left to right on identical hand types.
The result is then calculated by multiplying the bid number with the position in the sorted list and summing.

Part 2:
The solution to this is similar to part 1,
but when determining the hand type we replace the joker to get the best hand possible.
We also treat the joker as a 0 when comparing.
Sneaky edge case when all cards are jokers is treated as five of a kind of zeros.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        return solve(false);
    }

    @Override
    public String runPart2() throws IOException {
        return solve(true);
    }

    private String solve(boolean replaceJoker) throws IOException {
        List<Hand> hands = parseInput(replaceJoker)
                .stream()
                .sorted()
                .toList();
        long result = 0;
        for (int i = 0; i < hands.size(); i++) {
            result += (long) hands.get(i).bid * (i + 1);
        }
        return String.valueOf(result);
    }

    private List<Hand> parseInput(boolean replaceJoker) throws IOException {
        final Map<Character, Integer> cardValueMap = Map.of(
                'T', 10,
                'J', replaceJoker ? 0 : 11,
                'Q', 12,
                'K', 13,
                'A', 14
        );
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        return input.stream()
                .map(line -> parseInputLine(line, cardValueMap, replaceJoker))
                .collect(Collectors.toList());
    }

    private Hand parseInputLine(String inputLine, Map<Character, Integer> cardValueMap, boolean replaceJoker) {
        String[] components = inputLine.split(" ");
        List<Integer> cards = new ArrayList<>(5);
        for (char card : components[0].toCharArray()) {
            if (Character.isDigit(card)) {
                cards.add(card - '0');
            } else {
                cards.add(cardValueMap.get(card));
            }
        }

        int bid = Integer.parseInt(components[1]);

        return new Hand(cards, bid, replaceJoker);
    }

    private static class Hand implements Comparable<Hand> {
        private final List<Integer> cards;
        private final int bid;
        private final HandType handType;

        private Hand(List<Integer> cards, int bid, boolean replaceJoker) {
            this.cards = cards;
            this.bid = bid;
            this.handType = determineHandType(replaceJoker);
        }

        private Hand withJokerAsCard(int jokerCardValue) {
            List<Integer> newCards = cards.stream()
                    .map(card -> card == 0 ? jokerCardValue : card)
                    .toList();
            return new Hand(newCards, bid, false);
        }

        private Hand mapToHighestPossibleJokerHand(Hand hand) {
            return hand.cards.stream()
                    .filter(card -> card != 0)
                    .map(hand::withJokerAsCard)
                    .max(Comparator.naturalOrder())
                    .orElseGet(() -> new Hand(List.of(0, 0, 0, 0, 0), hand.bid, false));
        }

        private HandType determineHandType(boolean replaceJoker) {
            List<Integer> cards = replaceJoker ? mapToHighestPossibleJokerHand(this).cards : this.cards;
            Map<Integer, Integer> frequencyMap = new HashMap<>();
            cards.forEach(card -> frequencyMap.put(card, frequencyMap.getOrDefault(card, 0) + 1));
            return switch (frequencyMap.size()) {
                case 1 -> HandType.FIVE_OAK;
                case 2 -> {
                    if (frequencyMap.containsValue(4)) {
                        yield HandType.FOUR_OAK;
                    } else {
                        yield HandType.FULL;
                    }
                }
                case 3 -> {
                    if (frequencyMap.containsValue(3)) {
                        yield HandType.THREE_OAK;
                    } else {
                        yield HandType.PAIRS;
                    }
                }
                case 4 -> HandType.PAIR;
                default -> HandType.HIGH;
            };
        }

        @Override
        public int compareTo(@NotNull Solution.Hand other) {
            if (this.handType != other.handType) {
                return Integer.compare(this.handType.rank, other.handType.rank);
            } else {
                for (int i = 0; i < this.cards.size(); i++) {
                    if (Objects.equals(this.cards.get(i), other.cards.get(i))) {
                        continue;
                    }
                    if (this.cards.get(i) < other.cards.get(i)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
                return 0;
            }
        }
    }

    private enum HandType {
        FIVE_OAK(6),
        FOUR_OAK(5),
        FULL(4),
        THREE_OAK(3),
        PAIRS(2),
        PAIR(1),
        HIGH(0);

        private final int rank;

        HandType(int rank) {
            this.rank = rank;
        }
    }
}