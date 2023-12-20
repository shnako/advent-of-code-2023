package com.shnako.solutions.day20;

import com.shnako.SolutionBaseTest;
import com.shnako.solutions.SolutionBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest implements SolutionBaseTest {
    private final SolutionBase solution = new Solution();

    @Test
    public void testPart1() throws Exception {
        assertEquals("898731036", solution.runPart1());
    }

    @Test
    public void testPart2() throws Exception {
        // LCM of 3797, 3847, 3877, 4051
        assertEquals("229414480926893", solution.runPart2());
    }
}
