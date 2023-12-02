package com.shnako;

import com.shnako.solutions.SolutionBase;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        String day = getDay(args);

        SolutionBase solution = getSolutionInstance(day);
        if (solution == null) {
            System.out.printf("%nSolution for day %s not found.%n", day);
            System.exit(1);
            return;
        }

        int part = getPart(args);

        if (part == 1 || part == 0) {
            runPart(solution, day, 1);
        }

        if (part == 2 || part == 0) {
            runPart(solution, day, 2);
        }
    }

    private static void runPart(SolutionBase solution, String day, int part) throws Exception {
        System.out.printf("Running day %s part %d.", day, part);

        long start = System.currentTimeMillis();
        String result = switch (part) {
            case 1 -> solution.runPart1();
            case 2 -> solution.runPart2();
            default -> throw new IllegalStateException("Unexpected part: " + part);
        };
        long executionTime = System.currentTimeMillis() - start;

        System.out.printf("%nSolution for day %s part %d took %d ms to run. Result:%n", day, part, executionTime);
        System.out.println(result);
    }

    private static String getDay(String[] args) {
        String day;
        if (args.length > 0 && StringUtils.isNumeric(args[0])) {
            day = args[0];
        } else {
            Scanner in = new Scanner(System.in);
            System.out.print("Day: ");
            day = in.nextLine();
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        return day;
    }

    private static int getPart(String[] args) {
        int part = 0;
        if (args.length > 1 && StringUtils.isNumeric(args[1])) {
            if ("1".equals(args[1])) {
                part = 1;
            }
            if ("2".equals(args[1])) {
                part = 2;
            }
        } else {
            Scanner in = new Scanner(System.in);
            System.out.print("Part [1, 2 or 0 for both]: ");
            part = in.nextInt();
        }
        return part;
    }

    private static SolutionBase getSolutionInstance(String day) {
        String solutionClassName = Main.class.getPackageName() + ".solutions.day" + day + ".Solution";

        Class<?> solutionClass;
        try {
            solutionClass = Class.forName(solutionClassName);
        } catch (ClassNotFoundException ex) {
            return null;
        }

        try {
            return (SolutionBase) solutionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}