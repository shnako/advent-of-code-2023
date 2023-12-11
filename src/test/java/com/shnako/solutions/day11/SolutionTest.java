package com.shnako.solutions.day11;

import com.shnako.SolutionBaseTest;
import com.shnako.solutions.SolutionBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest implements SolutionBaseTest {
    private final SolutionBase solution = new Solution();

    @Test
    public void testPart1() throws Exception {
        assertEquals("9693756", solution.runPart1());
    }

    @Test
    public void testPart2() throws Exception {
        assertEquals("717878258016", solution.runPart2());
    }
}
