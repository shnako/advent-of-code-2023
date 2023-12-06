# Advent of Code 2023 :christmas_tree:

I tried to make the code as clean and easy to read as possible, and I've also provided a short explanation to most solutions so that they're easier to understand.

| Day | Puzzle                                                                 | Solution                                                                   | Results                                                                    | Input                                                               | Text                                                                  |
|-----|------------------------------------------------------------------------|----------------------------------------------------------------------------|----------------------------------------------------------------------------|---------------------------------------------------------------------|-----------------------------------------------------------------------|
| 01  | [Trebuchet?!](https://adventofcode.com/2023/day/1)                     | [Day 01 solution](/src/main/java/com/shnako/solutions/day01/Solution.java) | [Day 01 test](/src/test/java/com/shnako/solutions/day01/SolutionTest.java) | [Day 01 input](/src/main/java/com/shnako/solutions/day01/input.txt) | [Day 01 puzzle](/src/main/java/com/shnako/solutions/day01/puzzle.txt) |
| 02  | [Cube Conundrum](https://adventofcode.com/2023/day/2)                  | [Day 02 solution](/src/main/java/com/shnako/solutions/day02/Solution.java) | [Day 02 test](/src/test/java/com/shnako/solutions/day02/SolutionTest.java) | [Day 02 input](/src/main/java/com/shnako/solutions/day02/input.txt) | [Day 02 puzzle](/src/main/java/com/shnako/solutions/day02/puzzle.txt) |
| 03  | [Gear Ratios](https://adventofcode.com/2023/day/3)                     | [Day 03 solution](/src/main/java/com/shnako/solutions/day03/Solution.java) | [Day 03 test](/src/test/java/com/shnako/solutions/day03/SolutionTest.java) | [Day 03 input](/src/main/java/com/shnako/solutions/day03/input.txt) | [Day 03 puzzle](/src/main/java/com/shnako/solutions/day03/puzzle.txt) |
| 04  | [Scratchcards](https://adventofcode.com/2023/day/4)                    | [Day 04 solution](/src/main/java/com/shnako/solutions/day04/Solution.java) | [Day 04 test](/src/test/java/com/shnako/solutions/day04/SolutionTest.java) | [Day 04 input](/src/main/java/com/shnako/solutions/day04/input.txt) | [Day 04 puzzle](/src/main/java/com/shnako/solutions/day04/puzzle.txt) |
| 05  | [If You Give A Seed A Fertilizer](https://adventofcode.com/2023/day/5) | [Day 05 solution](/src/main/java/com/shnako/solutions/day05/Solution.java) | [Day 05 test](/src/test/java/com/shnako/solutions/day05/SolutionTest.java) | [Day 05 input](/src/main/java/com/shnako/solutions/day05/input.txt) | [Day 05 puzzle](/src/main/java/com/shnako/solutions/day05/puzzle.txt) |
| 06  | [Wait For It](https://adventofcode.com/2023/day/6)                     | [Day 06 solution](/src/main/java/com/shnako/solutions/day06/Solution.java) | [Day 06 test](/src/test/java/com/shnako/solutions/day06/SolutionTest.java) | [Day 06 input](/src/main/java/com/shnako/solutions/day06/input.txt) | [Day 06 puzzle](/src/main/java/com/shnako/solutions/day06/puzzle.txt) |

## Structure

### [Solutions](/src/main/java/com/shnako/solutions)
The corresponding files for each solution are grouped within a dayXX package. Each package contains:
- Solution.java: my implementation of the solution to the puzzle
- puzzle.txt: a dump of the puzzle text from the website, with the source link at the top
- input.txt: the puzzle input I received

Each solution extends the [SolutionBase](/src/main/java/com/shnako/solutions/SolutionBase.java) interface and must implement the [runPart1](/src/main/java/com/shnako/solutions/SolutionBase.java#L6) and [runPart2](/src/main/java/com/shnako/solutions/SolutionBase.java#L8) methods which return the result as a string.

### [Tests and results](/src/test/java/com/shnako/solutions)
Each test implements the [SolutionBaseTest](/src/test/java/com/shnako/SolutionBaseTest.java) interface and must specify the expected results in the test assertions.

## Running

### All solutions via tests
You can run all the tests using gradle:

    ./gradlew test

or if you're using Windows:

    gradlew.bat test

### Individual solutions via main
You can also take advantage of the functionality implemented in [Main.java](/src/main/java/com/shnako/Main.java) to run the puzzles individually:

    ./gradlew run --args='<day> <part>'

or if you're using Windows:

    gradlew.bat run --args="<day> <part>"

replacing `<day>` with the puzzle's day number and `<part>` with either 1 or 2, or 0 to run both parts.

Running without the arguments will prompt you to enter them.