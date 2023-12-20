package com.shnako.solutions.day20;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
We represent pulses as boolean objects, with high pulses as true and low pulses as false.
We've implemented this in an object-oriented way, storing the input using a Module abstract class
extended by FlipFlopModule, ConjunctionModule and BroadcasterModule, each implementing its custom operations.
Because we need to process each module's pulses before processing the pulses those pulses caused,
we need to use a BFS algorithm.
For each module, we store all its upcoming pulses in a queue.
We then store each upcoming module in another queue.
Everytime we take a module off the queue, we process its pulses and add them to both queues,
continuing until the modules queue is empty, which means the button press has caused all its effects.
We use global variables as passing all the variables as parameters would be too messy.

Part 1:
We keep counters for low and high pulses and increment them every time a pulse is sent.
The result is the number of low pulses multiplied by the number of high pulses after 1000 button presses.

Part 2:
The number of button presses required to send a signal to the button is far too large to iterate to.
By looking at the input, we can see that there is a ConjunctionNode connected to rx (parent)
and 4 ConjunctionNodes connected to the parent (grandparents).
In order for the parent to send a low pulse to rx, all its inputs (grandparents) must have sent high pulses previously.
We can then find how many button presses it takes for each of the grandparents to send a high pulse.
The result is therefore the LCM of the grandparents' cycles.
 */
public class Solution extends SolutionBase {
    private int part;
    private Map<String, Module> modules;
    private Map<Boolean, Long> pulseCounter;
    private LinkedList<Module> moduleProcessingQueue;
    private List<String> grandparentsOfOutput;
    private Map<String, Integer> foundCycleLengths;
    private int buttonPress;

    @Override
    public String runPart1() throws IOException {
        part = 1;
        modules = parseInput();
        pulseCounter = new HashMap<>() {{
            put(false, 0L);
            put(true, 0L);
        }};
        moduleProcessingQueue = new LinkedList<>();

        for (buttonPress = 1; buttonPress <= 1000; buttonPress++) {
            modules.get("broadcaster").addPulseToQueue(new Pulse(false, "button"));
            pulseCounter.put(false, pulseCounter.get(false) + 1);
            while (!moduleProcessingQueue.isEmpty()) {
                Module module = moduleProcessingQueue.pop();
                module.processPulse();
            }
        }

        long result = pulseCounter.get(false) * pulseCounter.get(true);
        return String.valueOf(result);
    }

    @Override
    public String runPart2() throws IOException {
        part = 2;
        modules = parseInput();

        moduleProcessingQueue = new LinkedList<>();
        grandparentsOfOutput = findGrandparentsOfRx();
        foundCycleLengths = new HashMap<>(grandparentsOfOutput.size());

        for (buttonPress = 1; grandparentsOfOutput.size() != foundCycleLengths.size(); buttonPress++) {
            modules.get("broadcaster").addPulseToQueue(new Pulse(false, "button"));
            while (!moduleProcessingQueue.isEmpty()) {
                Module module = moduleProcessingQueue.pop();
                module.processPulse();
            }
        }
        BigInteger result = foundCycleLengths.values()
                .stream()
                .map(BigInteger::valueOf)
                .reduce(BigInteger.ONE, this::lcm);

        return String.valueOf(result);
    }

    public BigInteger lcm(BigInteger number1, BigInteger number2) {
        BigInteger gcd = number1.gcd(number2);
        BigInteger absProduct = number1.multiply(number2).abs();
        return absProduct.divide(gcd);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private List<String> findGrandparentsOfRx() {
        String parentId = modules.values()
                .stream()
                .filter(x -> x.destinationModuleIds.contains("rx"))
                .findFirst()
                .get()
                .id;

        return modules.values()
                .stream()
                .filter(x -> x.destinationModuleIds.contains(parentId))
                .map(x -> x.id)
                .toList();
    }

    private Map<String, Module> parseInput() throws IOException {
        Map<String, Module> result = InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(this::parseInputLine)
                .collect(Collectors.toMap(module -> module.id, module -> module));

        result.values()
                .forEach(module -> module.destinationModuleIds
                        .stream()
                        .filter(dmId -> result.get(dmId) instanceof ConjunctionModule)
                        .forEach(dmId -> ((ConjunctionModule) result.get(dmId)).addInputModule(module.id)));

        return result;
    }

    private Module parseInputLine(String line) {
        String[] components = line.split(" -> ");
        List<String> destinationModuleIds = List.of(components[1].split(", "));
        return switch (components[0].charAt(0)) {
            case 'b' -> new BroadcasterModule(components[0], destinationModuleIds);
            case '%' -> new FlipFlopModule(components[0].substring(1), destinationModuleIds);
            case '&' -> new ConjunctionModule(components[0].substring(1), destinationModuleIds);
            default -> throw new RuntimeException("Invalid module type id found.");
        };
    }

    private class FlipFlopModule extends Module {
        private boolean isFlipOn;

        private FlipFlopModule(String id, List<String> destinationModuleIds) {
            super(id, destinationModuleIds);
            isFlipOn = false;
        }

        @Override
        void processPulse(Pulse pulse) {
            if (!pulse.isHighPulse) {
                isFlipOn = !isFlipOn;
                sendPulses(isFlipOn);
            }
        }
    }

    private class ConjunctionModule extends Module {
        private final Map<String, Boolean> latestReceivedPulses;

        private ConjunctionModule(String id, List<String> destinationModuleIds) {
            super(id, destinationModuleIds);
            latestReceivedPulses = new HashMap<>();
        }

        void addInputModule(String id) {
            latestReceivedPulses.put(id, false);
        }

        @Override
        void processPulse(Pulse pulse) {
            latestReceivedPulses.put(pulse.fromModuleId, pulse.isHighPulse);
            sendPulses(!latestReceivedPulses.values().stream().allMatch(isHighPulse -> isHighPulse));

            if (part == 2
                    && latestReceivedPulses.values().stream().noneMatch(isHighPulse -> isHighPulse)
                    && grandparentsOfOutput.contains(id)
                    && !foundCycleLengths.containsKey(id)) {
                foundCycleLengths.put(id, buttonPress);
            }
        }
    }

    private class BroadcasterModule extends Module {
        private BroadcasterModule(String id, List<String> destinationModuleIds) {
            super(id, destinationModuleIds);
        }

        @Override
        void processPulse(Pulse pulse) {
            sendPulses(false);
        }
    }

    private abstract class Module {
        final String id;
        final List<String> destinationModuleIds;
        final LinkedList<Pulse> incomingPulseQueue;

        Module(String id, List<String> destinationModuleIds) {
            this.id = id;
            this.destinationModuleIds = destinationModuleIds;
            this.incomingPulseQueue = new LinkedList<>();
        }

        void sendPulses(boolean isHighPulse) {
            Pulse pulse = new Pulse(isHighPulse, id);
            for (String destinationModuleId : destinationModuleIds) {
                if (modules.containsKey(destinationModuleId)) {
                    modules.get(destinationModuleId).addPulseToQueue(pulse);
                }
            }
            if (part == 1) {
                pulseCounter.put(pulse.isHighPulse, pulseCounter.get(pulse.isHighPulse) + destinationModuleIds.size());
            }
        }

        void addPulseToQueue(Pulse pulse) {
            incomingPulseQueue.add(pulse);
            moduleProcessingQueue.add(this);
        }

        void processPulse() {
            Pulse pulse = incomingPulseQueue.pop();
            processPulse(pulse);
            // System.out.println(pulse.fromModuleId + " -" + (pulse.isHighPulse ? "high" : "low") + "-> " + id);
        }

        abstract void processPulse(Pulse pulse);
    }

    private record Pulse(boolean isHighPulse, String fromModuleId) {
    }
}