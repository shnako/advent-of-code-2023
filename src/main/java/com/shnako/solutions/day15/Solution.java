package com.shnako.solutions.day15;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Part 1:
We simply calculate the hashes as detailed, taking advantage of the fact that Java treats chars as ASCII integers.
The result is the sum of the hashes.

Part 2:
We store the operations in LensOperation objects, which implement equals() and hashCode() on the label only.
This allows us to easily identify ones with identical in the boxes, while also keeping track of their focal lengths.
The result is the sum calculated as detailed in the requirements.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        String input = InputProcessingUtil.readInputLines(getDay()).get(0);
        int result = Arrays.stream(input.split(","))
                .mapToInt(this::calculateHash)
                .sum();
        return String.valueOf(result);
    }

    private int calculateHash(String str) {
        int result = 0;
        for (char c : str.toCharArray()) {
            result += c;
            result *= 17;
            result %= 256;
        }
        return result;
    }

    @Override
    public String runPart2() throws IOException {
        String input = InputProcessingUtil.readInputLines(getDay()).get(0);

        List<List<LensOperation>> boxes = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            boxes.add(new ArrayList<>());
        }

        Arrays.stream(input.split(","))
                .map(LensOperation::new)
                .forEach(lo -> executeOperation(lo, boxes));

        int result = 0;
        for (int boxI = 0; boxI < boxes.size(); boxI++) {
            for (int slotI = 0; slotI < boxes.get(boxI).size(); slotI++) {
                int lensFocusingPower = (boxI + 1) * (slotI + 1) * boxes.get(boxI).get(slotI).focalLength;
                result += lensFocusingPower;
            }
        }

        return String.valueOf(result);
    }

    private void executeOperation(LensOperation lensOperation, List<List<LensOperation>> boxes) {
        if (lensOperation.focalLength == -1) {
            boxes.get(lensOperation.hash).remove(lensOperation);
        } else if (boxes.get(lensOperation.hash).contains(lensOperation)) {
            int storedLensIndex = boxes.get(lensOperation.hash).indexOf(lensOperation);
            boxes.get(lensOperation.hash).get(storedLensIndex).focalLength = lensOperation.focalLength;
        } else {
            boxes.get(lensOperation.hash).add(lensOperation);
        }
    }

    private class LensOperation {
        private final String label;
        private final int hash;
        private int focalLength;

        private LensOperation(String input) {
            if (input.contains("=")) {
                String[] components = input.split("=");
                label = components[0];
                focalLength = Integer.parseInt(components[1]);
            } else {
                label = input.substring(0, input.length() - 1);
                focalLength = -1;
            }
            hash = calculateHash(label);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LensOperation that = (LensOperation) o;

            return label.equals(that.label);
        }

        @Override
        public int hashCode() {
            return label.hashCode();
        }
    }
}