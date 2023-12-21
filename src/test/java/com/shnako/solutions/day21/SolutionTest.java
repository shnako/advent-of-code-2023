package com.shnako.solutions.day21;

import com.shnako.SolutionBaseTest;
import com.shnako.solutions.SolutionBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest implements SolutionBaseTest {
    private final SolutionBase solution = new Solution();

    @Test
    public void testPart1() throws Exception {
        assertEquals("3820", solution.runPart1());
    }

    @Test
    public void testPart2() throws Exception {
        // TOO LOW:  470183000000000
        // TOO LOW:  561326000000000
        // TOO LOW:  627471000000000
        // TOO HIGH: 691916000000000

        assertEquals("632421652138917", solution.runPart2());
    }
}
